package co.edu.ucc.clinica.historias.aplicacion.servicios;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.*;
import co.edu.ucc.clinica.historias.dominio.*;

/** Los tres casos de uso de modificación comparten el guion de ModificadorDeHistorias. */
class ModificacionesDeHistoriaTest {
    final Falsos f = new Falsos();
    final RegistrarAlergiaUseCase alergias = new RegistrarAlergiaService(f.modificador, f.reloj);
    final RegistrarAntecedenteUseCase antecedentes = new RegistrarAntecedenteService(f.modificador, f.reloj);
    final AgregarNotaUseCase notas = new AgregarNotaService(f.modificador, f.reloj);

    @Test
    void registrarAlergiaGuardaYDejaRastro() {
        f.conPaciente("1085");
        alergias.registrar(new RegistrarAlergiaUseCase.Comando("enf-01", Ambito.URGENCIAS, "1085", "Látex",
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
        assertThrows(AccesoDenegadoException.class, () -> antecedentes.registrar(
                new RegistrarAntecedenteUseCase.Comando("enf-01", Ambito.CONSULTORIO, "1085", "Asma")));
        assertEquals(0, f.historias.guardadas);
        assertEquals(ResultadoAcceso.DENEGADO, f.bitacora.registros.getFirst().resultado());
    }

    @Test
    void siLaReglaFallaNoSeGuardaNiSeRegistraModificacion() {
        f.conPaciente("1085");
        assertThrows(ReglaDeNegocioVioladaException.class, () -> alergias.registrar(
                new RegistrarAlergiaUseCase.Comando("med-01", Ambito.CONSULTORIO, "1085", "Penicilina",
                        Severidad.GRAVE, null)));
        assertEquals(0, f.historias.guardadas);
        assertTrue(f.bitacora.registros.isEmpty());
    }

    @Test
    void laNotaQuedaConElTipoDelRol() {
        f.conPaciente("1085");
        notas.agregar(new AgregarNotaUseCase.Comando("enf-01", Ambito.CONSULTORIO, "1085", "TA 120/80"));
        assertEquals(TipoNota.NOTA_ENFERMERIA, f.historias.datos.get("1085").notas().getFirst().tipo());
        assertEquals("Nota clínica", f.bitacora.registros.getFirst().detalle());
    }

    @Test
    void noSeModificaUnaHistoriaQueNoExiste() {
        assertThrows(HistoriaNoEncontradaException.class, () -> antecedentes.registrar(
                new RegistrarAntecedenteUseCase.Comando("med-01", Ambito.CONSULTORIO, "999", "Asma")));
    }
}
