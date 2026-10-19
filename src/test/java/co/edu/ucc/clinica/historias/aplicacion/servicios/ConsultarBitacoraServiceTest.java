package co.edu.ucc.clinica.historias.aplicacion.servicios;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarHistoriaUseCase.Solicitud;
import co.edu.ucc.clinica.historias.dominio.*;

class ConsultarBitacoraServiceTest {
    final Falsos f = new Falsos();
    final ConsultarHistoriaService consultas = new ConsultarHistoriaService(f.historias, f.trazabilidad);
    final ConsultarBitacoraService servicio = new ConsultarBitacoraService(f.bitacora, f.trazabilidad);

    @Test
    void elAuditorVeQuienConsultoYCuando() {
        f.conPaciente("1085");
        consultas.consultar(new Solicitud("med-01", Ambito.CONSULTORIO, "1085", "Control"));
        consultas.resumenDeUrgencias(new Solicitud("enf-01", Ambito.URGENCIAS, "1085", null));

        var registros = servicio.consultar("aud-01", Ambito.CONSULTORIO, "1085");

        assertEquals(2, registros.size());
        assertEquals("med-01", registros.get(0).profesionalId());
        assertEquals("enf-01", registros.get(1).profesionalId());
        assertEquals(TipoAcceso.AUDITORIA, f.bitacora.registros.getLast().tipo()); // la revisión también queda
    }

    @Test
    void unMedicoNoPuedeVerLaBitacora() {
        assertThrows(AccesoDenegadoException.class, () -> servicio.consultar("med-01", Ambito.CONSULTORIO, "1085"));
        assertEquals(ResultadoAcceso.DENEGADO, f.bitacora.registros.getFirst().resultado());
    }
}
