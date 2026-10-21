package co.edu.ucc.clinica.historias.dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AmbitoTest {

    @Test
    void cadaAmbitoDecideSiExigeMotivoYComoSeMarcaElAcceso() {
        assertTrue(Ambito.CONSULTORIO.exigeMotivo());
        assertEquals(TipoAcceso.CONSULTA, Ambito.CONSULTORIO.tipoDeConsulta());
        assertFalse(Ambito.URGENCIAS.exigeMotivo());
        assertEquals(TipoAcceso.CONSULTA_URGENCIAS, Ambito.URGENCIAS.tipoDeConsulta());
    }

    @Test
    void soloLaConsultaDeUrgenciasRequiereRevisionPosterior() {
        for (TipoAcceso t : TipoAcceso.values())
            assertEquals(t == TipoAcceso.CONSULTA_URGENCIAS, t.requiereRevisionPosterior(), t.name());
    }
}
