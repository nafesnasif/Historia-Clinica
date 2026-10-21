package co.edu.ucc.clinica.historias.aplicacion.servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import co.edu.ucc.clinica.historias.dominio.*;

/** Abierto/cerrado: una reacción nueva a los accesos se agrega sin tocar RastroDeAccesos. */
class RastroDeAccesosTest {
    final Falsos f = new Falsos();
    final Profesional medico = new Profesional("med-01", "Dra. Ana Rosero", Rol.MEDICO);

    @Test
    void unOyenteNuevoRecibeCadaRegistroSinModificarElRastro() {
        List<RegistroAcceso> vistos = new ArrayList<>();
        OyenteDeAccesos contador = vistos::add; // una métrica, por ejemplo
        var rastro = new RastroDeAccesos(f.bitacora, List.of(new AvisoAlOficialDeDatos(f.notificador), contador),
                f.reloj);

        rastro.permitido("1085", medico, Ambito.URGENCIAS, TipoAcceso.CONSULTA_URGENCIAS, "Resumen");
        rastro.denegado("1085", medico, Ambito.CONSULTORIO, TipoAcceso.CONSULTA, "Sin motivo");

        assertEquals(2, vistos.size());
        assertEquals(2, f.bitacora.registros.size());
        assertEquals(1, f.notificador.alertas.size());
    }

    @Test
    void unAccesoDeUrgenciasDenegadoNoSeAvisaAlOficial() {
        f.rastro.denegado("1085", medico, Ambito.URGENCIAS, TipoAcceso.CONSULTA_URGENCIAS, "Rol sin permiso");
        assertTrue(f.notificador.alertas.isEmpty());
    }

    @Test
    void laNegativaDelDominioQuedaRegistradaYSeRelanza() {
        assertThrows(AccesoDenegadoException.class, () -> f.rastro.registrandoNegativas("1085", medico,
                Ambito.CONSULTORIO, TipoAcceso.MODIFICACION, () -> {
                    throw new AccesoDenegadoException("No");
                }));
        assertEquals(ResultadoAcceso.DENEGADO, f.bitacora.registros.getFirst().resultado());
        assertEquals("No", f.bitacora.registros.getFirst().detalle());
    }
}
