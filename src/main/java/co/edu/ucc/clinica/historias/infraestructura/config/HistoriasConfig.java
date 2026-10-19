package co.edu.ucc.clinica.historias.infraestructura.config;

import java.time.Instant;
import java.util.function.Supplier;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.AbrirHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ActualizarHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarBitacoraUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.BitacoraDeAccesos;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.DirectorioDeProfesionales;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Notificador;
import co.edu.ucc.clinica.historias.aplicacion.servicios.AbrirHistoriaService;
import co.edu.ucc.clinica.historias.aplicacion.servicios.ActualizarHistoriaService;
import co.edu.ucc.clinica.historias.aplicacion.servicios.ConsultarBitacoraService;
import co.edu.ucc.clinica.historias.aplicacion.servicios.ConsultarHistoriaService;
import co.edu.ucc.clinica.historias.aplicacion.servicios.Trazabilidad;
import co.edu.ucc.clinica.historias.dominio.AccesoDenegadoException;
import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;
import co.edu.ucc.clinica.historias.dominio.ResumenDeUrgencias;

@Configuration
class HistoriasConfig {

    @Bean
    Trazabilidad trazabilidad(DirectorioDeProfesionales directorio, BitacoraDeAccesos bitacora,
                              Notificador notificador) {
        return new Trazabilidad(directorio, bitacora, notificador, Instant::now);
    }

    @Bean
    AbrirHistoriaUseCase abrirHistoria(HistoriaClinicaRepository historias, Trazabilidad trazabilidad,
                                       ObjectProvider<PlatformTransactionManager> transacciones) {
        var servicio = new AbrirHistoriaService(historias, trazabilidad);
        var tx = new Transacciones(transacciones.getIfAvailable());
        return comando -> tx.ejecutar(() -> servicio.abrir(comando));
    }

    @Bean
    ConsultarHistoriaUseCase consultarHistoria(HistoriaClinicaRepository historias, Trazabilidad trazabilidad,
                                               ObjectProvider<PlatformTransactionManager> transacciones) {
        var servicio = new ConsultarHistoriaService(historias, trazabilidad);
        var tx = new Transacciones(transacciones.getIfAvailable());
        return new ConsultarHistoriaUseCase() {
            public HistoriaClinica consultar(Solicitud s) { return tx.ejecutar(() -> servicio.consultar(s)); }
            public ResumenDeUrgencias resumenDeUrgencias(Solicitud s) {
                return tx.ejecutar(() -> servicio.resumenDeUrgencias(s));
            }
        };
    }

    @Bean
    ActualizarHistoriaUseCase actualizarHistoria(HistoriaClinicaRepository historias, Trazabilidad trazabilidad,
                                                 ObjectProvider<PlatformTransactionManager> transacciones) {
        var servicio = new ActualizarHistoriaService(historias, trazabilidad);
        var tx = new Transacciones(transacciones.getIfAvailable());
        return new ActualizarHistoriaUseCase() {
            public String registrarAlergia(ComandoAlergia c) { return tx.ejecutar(() -> servicio.registrarAlergia(c)); }
            public String registrarAntecedente(ComandoAntecedente c) {
                return tx.ejecutar(() -> servicio.registrarAntecedente(c));
            }
            public String agregarNota(ComandoNota c) { return tx.ejecutar(() -> servicio.agregarNota(c)); }
            public String prescribir(ComandoPrescripcion c) { return tx.ejecutar(() -> servicio.prescribir(c)); }
            public String registrarAdministracion(ComandoAdministracion c) {
                return tx.ejecutar(() -> servicio.registrarAdministracion(c));
            }
            public String reportarEventoAdverso(ComandoEventoAdverso c) {
                return tx.ejecutar(() -> servicio.reportarEventoAdverso(c));
            }
        };
    }

    @Bean
    ConsultarBitacoraUseCase consultarBitacora(BitacoraDeAccesos bitacora, Trazabilidad trazabilidad,
                                               ObjectProvider<PlatformTransactionManager> transacciones) {
        var servicio = new ConsultarBitacoraService(bitacora, trazabilidad);
        var tx = new Transacciones(transacciones.getIfAvailable());
        return (auditorId, ambito, documento) -> tx.ejecutar(() -> servicio.consultar(auditorId, ambito, documento));
    }

    /**
     * El cambio en la historia y su línea de bitácora se guardan juntos o no se guarda ninguno.
     * Un acceso denegado no cambió nada, pero su registro en la bitácora sí debe quedar:
     * por eso se confirma la transacción y luego se relanza la excepción.
     */
    static class Transacciones {
        private final TransactionTemplate tx;

        Transacciones(PlatformTransactionManager tm) {
            this.tx = tm == null ? null : new TransactionTemplate(tm); // perfil memoria: no hay transacciones
        }

        <T> T ejecutar(Supplier<T> trabajo) {
            if (tx == null)
                return trabajo.get();
            var denegado = new AccesoDenegadoException[1];
            T resultado = tx.execute(estado -> {
                try {
                    return trabajo.get();
                } catch (AccesoDenegadoException e) {
                    denegado[0] = e;
                    return null;
                }
            });
            if (denegado[0] != null)
                throw denegado[0];
            return resultado;
        }
    }
}
