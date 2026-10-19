package co.edu.ucc.clinica.historias.dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PoliticaDeAccesoTest {
    final Profesional medico = new Profesional("med-01", "Dra. Ana Rosero", Rol.MEDICO);
    final Profesional auditor = new Profesional("aud-01", "Carmen Ortiz", Rol.AUDITOR);

    @Test
    void enUrgenciasNuncaSeBloqueaAunqueNoHayaMotivo() {
        assertEquals(TipoAcceso.CONSULTA_URGENCIAS,
                PoliticaDeAcceso.autorizarConsulta(medico, Ambito.URGENCIAS, null));
    }

    @Test
    void enConsultorioSeExigeMotivo() {
        var error = assertThrows(AccesoDenegadoException.class,
                () -> PoliticaDeAcceso.autorizarConsulta(medico, Ambito.CONSULTORIO, " "));
        assertEquals("En consultorio se debe indicar el motivo de la consulta", error.getMessage());
        assertEquals(TipoAcceso.CONSULTA,
                PoliticaDeAcceso.autorizarConsulta(medico, Ambito.CONSULTORIO, "Control de tensión"));
    }

    @Test
    void elAuditorNoVeElContenidoClinicoNiSiquieraEnUrgencias() {
        assertThrows(AccesoDenegadoException.class,
                () -> PoliticaDeAcceso.autorizarConsulta(auditor, Ambito.URGENCIAS, "Revisión"));
    }

    @Test
    void soloElAuditorVeLaBitacora() {
        assertDoesNotThrow(() -> PoliticaDeAcceso.autorizarAuditoria(auditor));
        assertThrows(AccesoDenegadoException.class, () -> PoliticaDeAcceso.autorizarAuditoria(medico));
    }
}
