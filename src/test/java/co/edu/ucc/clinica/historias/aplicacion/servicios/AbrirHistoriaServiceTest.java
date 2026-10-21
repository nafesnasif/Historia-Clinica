package co.edu.ucc.clinica.historias.aplicacion.servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.AbrirHistoriaUseCase.Comando;
import co.edu.ucc.clinica.historias.dominio.*;

class AbrirHistoriaServiceTest {
    final Falsos f = new Falsos();
    final AbrirHistoriaService servicio = new AbrirHistoriaService(f.historias, f.identificacion, f.rastro, f.reloj);

    @Test
    void abrirDejaRastroDeApertura() {
        servicio.abrir(new Comando("enf-01", Ambito.URGENCIAS, "5555", "Pedro Ortega", LocalDate.of(2001, 3, 4)));
        assertTrue(f.historias.existe("5555"));
        assertEquals(TipoAcceso.APERTURA, f.bitacora.registros.getFirst().tipo());
    }

    @Test
    void unDocumentoConEspaciosNoSobrescribeLaHistoriaExistente() {
        f.conPaciente("1085");
        assertThrows(ReglaDeNegocioVioladaException.class, () -> servicio.abrir(
                new Comando("med-01", Ambito.CONSULTORIO, " 1085 ", "Otra persona", LocalDate.of(1990, 1, 1))));
        assertEquals(1, f.historias.datos.get("1085").alergias().size()); // la alergia sigue ahí
        assertEquals(0, f.historias.guardadas);
    }
}
