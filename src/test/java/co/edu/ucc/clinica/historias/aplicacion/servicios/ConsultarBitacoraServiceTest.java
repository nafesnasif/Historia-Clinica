package co.edu.ucc.clinica.historias.aplicacion.servicios;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarBitacoraUseCase.Solicitud;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.SolicitudDeConsulta;
import co.edu.ucc.clinica.historias.dominio.*;

class ConsultarBitacoraServiceTest {
    final Falsos f = new Falsos();
    final ConsultarHistoriaService consultas = new ConsultarHistoriaService(f.lectura);
    final ConsultarResumenDeUrgenciasService resumen = new ConsultarResumenDeUrgenciasService(f.lectura, f.reloj);
    final ConsultarBitacoraService servicio = new ConsultarBitacoraService(f.bitacora, f.identificacion, f.rastro);

    @Test
    void elAuditorVeQuienConsultoYCuando() {
        f.conPaciente("1085");
        consultas.consultar(new SolicitudDeConsulta("med-01", Ambito.CONSULTORIO, "1085", "Control"));
        resumen.consultar(new SolicitudDeConsulta("enf-01", Ambito.URGENCIAS, "1085", null));

        var registros = servicio.consultar(new Solicitud("aud-01", Ambito.CONSULTORIO, "1085"));

        assertEquals(2, registros.size());
        assertEquals("med-01", registros.get(0).profesionalId());
        assertEquals("enf-01", registros.get(1).profesionalId());
        assertEquals(TipoAcceso.AUDITORIA, f.bitacora.registros.getLast().tipo()); // la revisión también queda
    }

    @Test
    void unMedicoNoPuedeVerLaBitacora() {
        assertThrows(AccesoDenegadoException.class,
                () -> servicio.consultar(new Solicitud("med-01", Ambito.CONSULTORIO, "1085")));
        assertEquals(ResultadoAcceso.DENEGADO, f.bitacora.registros.getFirst().resultado());
    }
}
