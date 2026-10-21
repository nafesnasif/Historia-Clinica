package co.edu.ucc.clinica.historias.dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

/** Los permisos de cada rol se leen en un solo lugar: el enum Rol. */
class RolTest {

    @Test
    void elAuditorSoloVeLaBitacora() {
        for (Permiso p : Permiso.values())
            assertEquals(p == Permiso.VER_BITACORA, Rol.AUDITOR.permite(p), p.name());
    }

    @Test
    void elMedicoHaceTodoMenosAuditar() {
        for (Permiso p : Permiso.values())
            assertEquals(p != Permiso.VER_BITACORA, Rol.MEDICO.permite(p), p.name());
    }

    @Test
    void laEnfermeraNoRegistraAntecedentesNiAudita() {
        assertFalse(Rol.ENFERMERA.permite(Permiso.REGISTRAR_ANTECEDENTE));
        assertFalse(Rol.ENFERMERA.permite(Permiso.VER_BITACORA));
        assertTrue(Rol.ENFERMERA.permite(Permiso.REGISTRAR_ALERGIA));
    }

    @Test
    void cadaRolAsistencialTieneSuTipoDeNota() {
        assertEquals(Optional.of(TipoNota.EVOLUCION_MEDICA), Rol.MEDICO.tipoDeNota());
        assertEquals(Optional.of(TipoNota.NOTA_ENFERMERIA), Rol.ENFERMERA.tipoDeNota());
        assertEquals(Optional.empty(), Rol.AUDITOR.tipoDeNota());
    }

    @Test
    void elMensajeDeNegacionNombraElRolYLaAccion() {
        var enfermera = new Profesional("enf-01", "Enf. Luis Benavides", Rol.ENFERMERA);
        var error = assertThrows(AccesoDenegadoException.class, () -> enfermera.exigir(Permiso.REGISTRAR_ANTECEDENTE));
        assertEquals("El rol ENFERMERA no puede registrar antecedentes", error.getMessage());
    }
}
