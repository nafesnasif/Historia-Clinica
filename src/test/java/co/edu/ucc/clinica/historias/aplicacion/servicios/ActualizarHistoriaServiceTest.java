package co.edu.ucc.clinica.historias.aplicacion.servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ActualizarHistoriaUseCase.ComandoAdministracion;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ActualizarHistoriaUseCase.ComandoAlergia;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ActualizarHistoriaUseCase.ComandoAntecedente;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ActualizarHistoriaUseCase.ComandoEventoAdverso;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ActualizarHistoriaUseCase.ComandoPrescripcion;
import co.edu.ucc.clinica.historias.dominio.*;

class ActualizarHistoriaServiceTest {
    final Falsos f = new Falsos();
    final ActualizarHistoriaService servicio = new ActualizarHistoriaService(f.historias, f.trazabilidad);

    @Test
    void registrarAlergiaGuardaYDejaRastro() {
        f.conPaciente("1085");
        servicio.registrarAlergia(new ComandoAlergia("enf-01", Ambito.URGENCIAS, "1085", "Látex",
                Severidad.MODERADA, "Dermatitis"));
        assertEquals(2, f.historias.datos.get("1085").alergias().size());
        assertEquals(1, f.historias.guardadas);
        var registro = f.bitacora.registros.getFirst();
        assertEquals(TipoAcceso.MODIFICACION, registro.tipo());
        assertEquals("enf-01", registro.profesionalId());
    }

    @Test
    void siElDominioNiegaNoSeGuardaPeroQuedaElIntento() {
        f.conPaciente("1085");
        assertThrows(AccesoDenegadoException.class, () -> servicio.registrarAntecedente(
                new ComandoAntecedente("enf-01", Ambito.CONSULTORIO, "1085", "Asma")));
        assertEquals(0, f.historias.guardadas);
        assertEquals(ResultadoAcceso.DENEGADO, f.bitacora.registros.getFirst().resultado());
    }

    @Test
    void siLaReglaFallaNoSeGuardaNiSeRegistraModificacion() {
        f.conPaciente("1085");
        assertThrows(ReglaDeNegocioVioladaException.class, () -> servicio.registrarAlergia(
                new ComandoAlergia("med-01", Ambito.CONSULTORIO, "1085", "Penicilina", Severidad.GRAVE, null)));
        assertEquals(0, f.historias.guardadas);
        assertTrue(f.bitacora.registros.isEmpty());
    }

    @Test
    void elMarCompletoGuardaYDejaRastroDeCadaPaso() {
        f.conPaciente("1085");
        String prescripcionId = servicio.prescribir(new ComandoPrescripcion("med-01", Ambito.CONSULTORIO, "1085",
                "Dipirona", "1 g", "IV", "Cada 8 horas"));
        servicio.registrarAdministracion(new ComandoAdministracion("enf-01", Ambito.CONSULTORIO, "1085",
                prescripcionId, EstadoAdministracion.ADMINISTRADA, null));
        servicio.reportarEventoAdverso(new ComandoEventoAdverso("enf-01", Ambito.CONSULTORIO, "1085",
                prescripcionId, "Shock anafiláctico", Severidad.GRAVE));

        var h = f.historias.datos.get("1085");
        assertEquals(1, h.administraciones().size());
        assertEquals(1, h.eventosAdversos().size());
        assertTrue(h.alergias().stream().anyMatch(a -> a.sustancia().equals("Dipirona")));
        assertEquals(3, f.historias.guardadas);
        assertEquals(List.of("Prescripción de Dipirona",
                        "MAR: dosis ADMINISTRADA de la prescripción " + prescripcionId,
                        "Evento adverso GRAVE de la prescripción " + prescripcionId),
                f.bitacora.registros.stream().map(RegistroAcceso::detalle).toList());
    }

    @Test
    void laEnfermeraQueIntentaPrescribirQuedaEnLaBitacora() {
        f.conPaciente("1085");
        assertThrows(AccesoDenegadoException.class, () -> servicio.prescribir(new ComandoPrescripcion("enf-01",
                Ambito.URGENCIAS, "1085", "Dipirona", "1 g", "IV", "Cada 8 horas")));
        assertEquals(0, f.historias.guardadas);
        assertEquals(ResultadoAcceso.DENEGADO, f.bitacora.registros.getFirst().resultado());
    }

    @Test
    void noSeModificaUnaHistoriaQueNoExiste() {
        assertThrows(HistoriaNoEncontradaException.class, () -> servicio.registrarAntecedente(
                new ComandoAntecedente("med-01", Ambito.CONSULTORIO, "999", "Asma")));
    }
}
