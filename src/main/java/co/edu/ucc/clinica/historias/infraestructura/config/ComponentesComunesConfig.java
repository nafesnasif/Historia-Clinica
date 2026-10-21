package co.edu.ucc.clinica.historias.infraestructura.config;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.DirectorioDeProfesionales;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.LectorDeHistorias;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Notificador;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.RegistroDeAccesos;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Reloj;
import co.edu.ucc.clinica.historias.aplicacion.servicios.AvisoAlOficialDeDatos;
import co.edu.ucc.clinica.historias.aplicacion.servicios.IdentificacionDeProfesionales;
import co.edu.ucc.clinica.historias.aplicacion.servicios.LecturaDeHistorias;
import co.edu.ucc.clinica.historias.aplicacion.servicios.ModificadorDeHistorias;
import co.edu.ucc.clinica.historias.aplicacion.servicios.OyenteDeAccesos;
import co.edu.ucc.clinica.historias.aplicacion.servicios.RastroDeAccesos;

/** Arma las piezas que comparten los casos de uso. Es el único lugar que conoce las clases concretas. */
@Configuration
class ComponentesComunesConfig {

    @Bean
    Reloj reloj() {
        return Instant::now;
    }

    /** Para sumar otra reacción a los accesos basta con declarar otro bean de tipo OyenteDeAccesos. */
    @Bean
    OyenteDeAccesos avisoAlOficialDeDatos(Notificador notificador) {
        return new AvisoAlOficialDeDatos(notificador);
    }

    @Bean
    RastroDeAccesos rastroDeAccesos(RegistroDeAccesos bitacora, List<OyenteDeAccesos> oyentes, Reloj reloj) {
        return new RastroDeAccesos(bitacora, oyentes, reloj);
    }

    @Bean
    IdentificacionDeProfesionales identificacionDeProfesionales(DirectorioDeProfesionales directorio,
                                                                RastroDeAccesos rastro) {
        return new IdentificacionDeProfesionales(directorio, rastro);
    }

    @Bean
    ModificadorDeHistorias modificadorDeHistorias(HistoriaClinicaRepository historias,
                                                  IdentificacionDeProfesionales identificacion,
                                                  RastroDeAccesos rastro) {
        return new ModificadorDeHistorias(historias, identificacion, rastro);
    }

    @Bean
    LecturaDeHistorias lecturaDeHistorias(LectorDeHistorias historias, IdentificacionDeProfesionales identificacion,
                                          RastroDeAccesos rastro) {
        return new LecturaDeHistorias(historias, identificacion, rastro);
    }

    /** Con base de datos hay transacciones; en el perfil memoria no hay gestor de transacciones. */
    @Bean
    EjecutorTransaccional ejecutorTransaccional(ObjectProvider<PlatformTransactionManager> transacciones) {
        PlatformTransactionManager gestor = transacciones.getIfAvailable();
        return gestor == null ? new SinTransaccion() : new TransaccionConBitacora(gestor);
    }
}
