package co.edu.ucc.clinica.historias.dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** MAR (registro de administración de medicamentos) y eventos adversos. */
class MedicacionTest {
    static final Instant AHORA = Instant.parse("2026-10-18T10:00:00Z");
    static final Profesional MEDICO = new Profesional("med-01", "Dra. Ana Rosero", Rol.MEDICO);
    static final Profesional ENFERMERA = new Profesional("enf-01", "Enf. Luis Benavides", Rol.ENFERMERA);
    static final Profesional AUDITOR = new Profesional("aud-01", "Carmen Ortiz", Rol.AUDITOR);

    HistoriaClinica historiaNueva() {
        return HistoriaClinica.abrir("1085", "María Guerrero", LocalDate.of(1980, 5, 12), MEDICO, AHORA);
    }

    Prescripcion dipirona(HistoriaClinica h) {
        return h.prescribir(MEDICO, "Dipirona", "1 g", "IV", "Cada 8 horas", AHORA);
    }

    @Test
    void soloElMedicoPrescribe() {
        var h = historiaNueva();
        assertThrows(AccesoDenegadoException.class,
                () -> h.prescribir(ENFERMERA, "Dipirona", "1 g", "IV", "Cada 8 horas", AHORA));
        assertTrue(h.prescripciones().isEmpty());
    }

    @Test
    void noSePrescribeUnMedicamentoAlQueElPacienteEsAlergico() {
        var h = historiaNueva();
        h.registrarAlergia(ENFERMERA, "Penicilina", Severidad.GRAVE, "Anafilaxia", AHORA);
        var error = assertThrows(ReglaDeNegocioVioladaException.class,
                () -> h.prescribir(MEDICO, " penicilina ", "1 millón UI", "IM", "Dosis única", AHORA));
        assertEquals("No se puede prescribir penicilina: el paciente es alérgico (GRAVE)", error.getMessage());
        assertTrue(h.prescripciones().isEmpty());
    }

    @Test
    void laEnfermeraRegistraLaDosisEnElMar() {
        var h = historiaNueva();
        var p = dipirona(h);
        var a = h.registrarAdministracion(ENFERMERA, p.id(), EstadoAdministracion.ADMINISTRADA, null, AHORA);
        assertEquals(p.id(), a.prescripcionId());
        assertEquals("enf-01", a.registradaPor());
    }

    @Test
    void elAuditorNoRegistraDosis() {
        var h = historiaNueva();
        var p = dipirona(h);
        assertThrows(AccesoDenegadoException.class, () -> h.registrarAdministracion(AUDITOR, p.id(),
                EstadoAdministracion.ADMINISTRADA, null, AHORA));
        assertTrue(h.administraciones().isEmpty());
    }

    @Test
    void omitirUnaDosisExigeElMotivo() {
        var h = historiaNueva();
        var p = dipirona(h);
        var error = assertThrows(ReglaDeNegocioVioladaException.class, () -> h.registrarAdministracion(ENFERMERA,
                p.id(), EstadoAdministracion.OMITIDA, " ", AHORA));
        assertEquals("Una dosis OMITIDA exige el motivo en la observación", error.getMessage());
        h.registrarAdministracion(ENFERMERA, p.id(), EstadoAdministracion.OMITIDA, "Paciente en ayuno", AHORA);
        assertEquals(1, h.administraciones().size());
    }

    @Test
    void noSeRegistraUnaDosisDeUnaPrescripcionQueNoExiste() {
        var h = historiaNueva();
        assertThrows(ReglaDeNegocioVioladaException.class, () -> h.registrarAdministracion(ENFERMERA, "no-existe",
                EstadoAdministracion.ADMINISTRADA, null, AHORA));
    }

    @Test
    void noSeReportaUnEventoAdversoSiNoSeAdministroElMedicamento() {
        var h = historiaNueva();
        var p = dipirona(h);
        h.registrarAdministracion(ENFERMERA, p.id(), EstadoAdministracion.RECHAZADA, "No quiso", AHORA);
        assertThrows(ReglaDeNegocioVioladaException.class,
                () -> h.reportarEventoAdverso(ENFERMERA, p.id(), "Hipotensión", Severidad.MODERADA, AHORA));
        assertTrue(h.eventosAdversos().isEmpty());
    }

    @Test
    void unEventoAdversoLeveNoRegistraAlergia() {
        var h = historiaNueva();
        var p = dipirona(h);
        h.registrarAdministracion(ENFERMERA, p.id(), EstadoAdministracion.ADMINISTRADA, null, AHORA);
        var e = h.reportarEventoAdverso(ENFERMERA, p.id(), "Sudoración", Severidad.LEVE, AHORA);
        assertEquals("Dipirona", e.medicamento());
        assertTrue(h.alergias().isEmpty());
    }

    @Test
    void unEventoAdversoGraveRegistraLaAlergiaYBloqueaNuevasDosisYOrdenes() {
        var h = historiaNueva();
        var p = dipirona(h);
        h.registrarAdministracion(ENFERMERA, p.id(), EstadoAdministracion.ADMINISTRADA, null, AHORA);
        h.reportarEventoAdverso(ENFERMERA, p.id(), "Shock anafiláctico", Severidad.GRAVE, AHORA);

        assertEquals(List.of("Dipirona"), h.alergias().stream().map(Alergia::sustancia).toList());
        assertEquals(Severidad.GRAVE, h.alergias().getFirst().severidad());
        assertThrows(ReglaDeNegocioVioladaException.class, () -> h.registrarAdministracion(ENFERMERA, p.id(),
                EstadoAdministracion.ADMINISTRADA, null, AHORA));
        assertThrows(ReglaDeNegocioVioladaException.class, () -> dipirona(h));
        // Registrar que la dosis se omitió sí se permite: es justo lo que hay que dejar escrito.
        h.registrarAdministracion(ENFERMERA, p.id(), EstadoAdministracion.OMITIDA, "Alergia nueva", AHORA);
    }

    @Test
    void unSegundoEventoGraveNoDuplicaLaAlergia() {
        var h = historiaNueva();
        var p = dipirona(h);
        h.registrarAdministracion(ENFERMERA, p.id(), EstadoAdministracion.ADMINISTRADA, null, AHORA);
        h.reportarEventoAdverso(ENFERMERA, p.id(), "Broncoespasmo", Severidad.GRAVE, AHORA);
        h.reportarEventoAdverso(MEDICO, p.id(), "Angioedema", Severidad.GRAVE, AHORA);
        assertEquals(2, h.eventosAdversos().size());
        assertEquals(1, h.alergias().size());
    }

    @Test
    void fueraDelDominioNadiePuedeCrearRegistrosDeMedicacionConNew() {
        for (Class<?> tipo : List.of(Prescripcion.class, Administracion.class, EventoAdverso.class)) {
            for (var constructor : tipo.getDeclaredConstructors()) {
                assertFalse(java.lang.reflect.Modifier.isPublic(constructor.getModifiers()),
                        tipo.getSimpleName() + " no debe tener constructor público");
            }
        }
    }
}
