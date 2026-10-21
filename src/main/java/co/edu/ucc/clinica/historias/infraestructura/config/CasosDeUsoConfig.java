package co.edu.ucc.clinica.historias.infraestructura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.AbrirHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.AgregarNotaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarBitacoraUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarResumenDeUrgenciasUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.RegistrarAlergiaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.RegistrarAntecedenteUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.ConsultaDeAccesos;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Reloj;
import co.edu.ucc.clinica.historias.aplicacion.servicios.AbrirHistoriaService;
import co.edu.ucc.clinica.historias.aplicacion.servicios.AgregarNotaService;
import co.edu.ucc.clinica.historias.aplicacion.servicios.ConsultarBitacoraService;
import co.edu.ucc.clinica.historias.aplicacion.servicios.ConsultarHistoriaService;
import co.edu.ucc.clinica.historias.aplicacion.servicios.ConsultarResumenDeUrgenciasService;
import co.edu.ucc.clinica.historias.aplicacion.servicios.RegistrarAlergiaService;
import co.edu.ucc.clinica.historias.aplicacion.servicios.RegistrarAntecedenteService;
import co.edu.ucc.clinica.historias.aplicacion.servicios.IdentificacionDeProfesionales;
import co.edu.ucc.clinica.historias.aplicacion.servicios.LecturaDeHistorias;
import co.edu.ucc.clinica.historias.aplicacion.servicios.ModificadorDeHistorias;
import co.edu.ucc.clinica.historias.aplicacion.servicios.RastroDeAccesos;

/**
 * Un bean por caso de uso. Cada servicio se envuelve en el ejecutor transaccional: como cada puerto
 * de entrada tiene un solo método, el envoltorio es una lambda de una línea.
 */
@Configuration
class CasosDeUsoConfig {

    @Bean
    AbrirHistoriaUseCase abrirHistoria(HistoriaClinicaRepository historias, IdentificacionDeProfesionales identificacion,
                                       RastroDeAccesos rastro, Reloj reloj, EjecutorTransaccional tx) {
        var servicio = new AbrirHistoriaService(historias, identificacion, rastro, reloj);
        return c -> tx.ejecutar(() -> servicio.abrir(c));
    }

    @Bean
    ConsultarHistoriaUseCase consultarHistoria(LecturaDeHistorias lectura, EjecutorTransaccional tx) {
        var servicio = new ConsultarHistoriaService(lectura);
        return s -> tx.ejecutar(() -> servicio.consultar(s));
    }

    @Bean
    ConsultarResumenDeUrgenciasUseCase consultarResumenDeUrgencias(LecturaDeHistorias lectura, Reloj reloj,
                                                                   EjecutorTransaccional tx) {
        var servicio = new ConsultarResumenDeUrgenciasService(lectura, reloj);
        return s -> tx.ejecutar(() -> servicio.consultar(s));
    }

    @Bean
    RegistrarAlergiaUseCase registrarAlergia(ModificadorDeHistorias modificador, Reloj reloj, EjecutorTransaccional tx) {
        var servicio = new RegistrarAlergiaService(modificador, reloj);
        return c -> tx.ejecutar(() -> servicio.registrar(c));
    }

    @Bean
    RegistrarAntecedenteUseCase registrarAntecedente(ModificadorDeHistorias modificador, Reloj reloj,
                                                     EjecutorTransaccional tx) {
        var servicio = new RegistrarAntecedenteService(modificador, reloj);
        return c -> tx.ejecutar(() -> servicio.registrar(c));
    }

    @Bean
    AgregarNotaUseCase agregarNota(ModificadorDeHistorias modificador, Reloj reloj, EjecutorTransaccional tx) {
        var servicio = new AgregarNotaService(modificador, reloj);
        return c -> tx.ejecutar(() -> servicio.agregar(c));
    }

    @Bean
    ConsultarBitacoraUseCase consultarBitacora(ConsultaDeAccesos bitacora, IdentificacionDeProfesionales identificacion,
                                               RastroDeAccesos rastro, EjecutorTransaccional tx) {
        var servicio = new ConsultarBitacoraService(bitacora, identificacion, rastro);
        return s -> tx.ejecutar(() -> servicio.consultar(s));
    }
}
