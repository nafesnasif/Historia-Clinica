package co.edu.ucc.clinica;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarResumenDeUrgenciasUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.SolicitudDeConsulta;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.dominio.Ambito;
import co.edu.ucc.clinica.historias.infraestructura.salida.memoria.HistoriaEnMemoria;

/** Solicitar MariaDB sin servidor disponible no debe impedir que Hospital funcione. */
@SpringBootTest(properties = {"DB_HOST=127.0.0.1", "DB_PORT=1", "spring.profiles.active=mariadb"})
class ArranqueSinBaseDeDatosTest {
    @Autowired
    ApplicationContext contexto;
    @Autowired
    Environment entorno;
    @Autowired
    ConsultarResumenDeUrgenciasUseCase resumen;

    @Test
    void arrancaEnMemoriaSinConexionNiDockerYPermiteConsultar() {
        assertTrue(Arrays.asList(entorno.getActiveProfiles()).contains("memoria"));
        assertFalse(Arrays.asList(entorno.getActiveProfiles()).contains("mariadb"));
        assertTrue(contexto.getBeansOfType(DataSource.class).isEmpty());
        assertInstanceOf(HistoriaEnMemoria.class, contexto.getBean(HistoriaClinicaRepository.class));
        assertEquals("1085123456", resumen.consultar(new SolicitudDeConsulta(
                "med-01", Ambito.URGENCIAS, "1085123456", null)).documentoPaciente());
    }
}
