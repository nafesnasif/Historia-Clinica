package co.edu.ucc.clinica.historias.dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class HistoriaClinicaTest {
    static final Instant AHORA = Instant.parse("2026-10-18T10:00:00Z");
    static final Profesional MEDICO = new Profesional("med-01", "Dra. Ana Rosero", Rol.MEDICO);
    static final Profesional ENFERMERA = new Profesional("enf-01", "Enf. Luis Benavides", Rol.ENFERMERA);
    static final Profesional AUDITOR = new Profesional("aud-01", "Carmen Ortiz", Rol.AUDITOR);

    HistoriaClinica historiaNueva() {
        return HistoriaClinica.abrir("1085", "María Guerrero", LocalDate.of(1980, 5, 12), MEDICO, AHORA);
    }

    @Test
    void elAuditorNoPuedeAbrirHistorias() {
        assertThrows(AccesoDenegadoException.class,
                () -> HistoriaClinica.abrir("1085", "María", LocalDate.of(1980, 5, 12), AUDITOR, AHORA));
    }

    @Test
    void noSeAbreConFechaDeNacimientoFutura() {
        var error = assertThrows(ReglaDeNegocioVioladaException.class,
                () -> HistoriaClinica.abrir("1085", "María", LocalDate.of(2030, 1, 1), MEDICO, AHORA));
        assertEquals("La fecha de nacimiento no es válida", error.getMessage());
    }

    @Test
    void laEnfermeraPuedeRegistrarAlergias() {
        var h = historiaNueva();
        h.registrarAlergia(ENFERMERA, "Penicilina", Severidad.GRAVE, "Anafilaxia", AHORA);
        assertEquals(1, h.alergias().size());
    }

    @Test
    void noSeRegistraDosVecesLaMismaAlergia() {
        var h = historiaNueva();
        h.registrarAlergia(MEDICO, "Penicilina", Severidad.GRAVE, "Anafilaxia", AHORA);
        var error = assertThrows(ReglaDeNegocioVioladaException.class,
                () -> h.registrarAlergia(ENFERMERA, " penicilina ", Severidad.LEVE, null, AHORA));
        assertEquals("La alergia a penicilina ya está registrada", error.getMessage());
        assertEquals(1, h.alergias().size());
    }

    @Test
    void soloElMedicoRegistraAntecedentes() {
        var h = historiaNueva();
        assertThrows(AccesoDenegadoException.class,
                () -> h.registrarAntecedente(ENFERMERA, "Diabetes tipo 2", AHORA));
        assertTrue(h.antecedentes().isEmpty());
    }

    @Test
    void elTipoDeNotaLoDefineElRol() {
        var h = historiaNueva();
        assertEquals(TipoNota.EVOLUCION_MEDICA, h.agregarNota(MEDICO, "Paciente estable", AHORA).tipo());
        assertEquals(TipoNota.NOTA_ENFERMERIA, h.agregarNota(ENFERMERA, "TA 120/80", AHORA).tipo());
    }

    @Test
    void elAuditorNoEscribeEnLaHistoria() {
        var h = historiaNueva();
        assertThrows(AccesoDenegadoException.class, () -> h.agregarNota(AUDITOR, "Hola", AHORA));
        assertTrue(h.notas().isEmpty());
    }

    @Test
    void elResumenDeUrgenciasMuestraPrimeroLasAlergiasGraves() {
        var h = historiaNueva();
        h.registrarAlergia(MEDICO, "Ibuprofeno", Severidad.LEVE, "Urticaria", AHORA);
        h.registrarAlergia(MEDICO, "Penicilina", Severidad.GRAVE, "Anafilaxia", AHORA);
        h.registrarAlergia(MEDICO, "Látex", Severidad.MODERADA, "Dermatitis", AHORA);
        var resumen = h.resumenDeUrgencias(AHORA);
        assertEquals(List.of("Penicilina", "Látex", "Ibuprofeno"),
                resumen.alergias().stream().map(Alergia::sustancia).toList());
        assertEquals(46, resumen.edad());
    }

    @Test
    void laEdadSeCalculaConLaFechaDeColombiaNoLaDeUtc() {
        var h = HistoriaClinica.abrir("1085", "María", LocalDate.of(1980, 10, 18), MEDICO, AHORA);
        // 18 oct 03:00 UTC = 17 oct 22:00 en Bogotá: todavía no cumple años
        assertEquals(45, h.edad(Instant.parse("2026-10-18T03:00:00Z")));
        assertEquals(46, h.edad(Instant.parse("2026-10-18T05:00:00Z")));
    }

    @Test
    void fueraDelDominioNadiePuedeCrearRegistrosClinicosConNew() {
        for (Class<?> tipo : List.of(Alergia.class, Antecedente.class, NotaClinica.class)) {
            for (var constructor : tipo.getDeclaredConstructors()) {
                assertFalse(java.lang.reflect.Modifier.isPublic(constructor.getModifiers()),
                        tipo.getSimpleName() + " no debe tener constructor público");
            }
        }
    }

    @Test
    void elDocumentoSeGuardaSinEspacios() {
        var h = HistoriaClinica.abrir(" 1085 ", "María", LocalDate.of(1980, 5, 12), MEDICO, AHORA);
        assertEquals("1085", h.documentoPaciente());
    }
}
