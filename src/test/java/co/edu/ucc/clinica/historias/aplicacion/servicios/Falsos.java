package co.edu.ucc.clinica.historias.aplicacion.servicios;

import java.time.Instant;
import java.util.*;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.*;
import co.edu.ucc.clinica.historias.dominio.*;

/** Adaptadores falsos: clases de pocas líneas, sin Mockito y sin Spring. */
class Falsos {
    static final Instant AHORA = Instant.parse("2026-10-18T10:00:00Z");

    static class Historias implements HistoriaClinicaRepository {
        final Map<String, HistoriaClinica> datos = new HashMap<>();
        int guardadas = 0;
        public void guardar(HistoriaClinica h) { datos.put(h.documentoPaciente(), h); guardadas++; }
        public Optional<HistoriaClinica> buscarPorDocumento(String d) { return Optional.ofNullable(datos.get(d)); }
        public boolean existe(String d) { return datos.containsKey(d); }
    }

    static class Bitacora implements BitacoraDeAccesos {
        final List<RegistroAcceso> registros = new ArrayList<>();
        public void registrar(RegistroAcceso r) { registros.add(r); }
        public List<RegistroAcceso> deHistoria(String d) {
            return registros.stream().filter(r -> r.documentoPaciente().equals(d)).toList();
        }
    }

    static class NotificadorFalso implements Notificador {
        final List<RegistroAcceso> alertas = new ArrayList<>();
        public void accesoDeUrgencias(RegistroAcceso r) { alertas.add(r); }
    }

    static final DirectorioDeProfesionales DIRECTORIO = id -> Optional.ofNullable(Map.of(
            "med-01", new Profesional("med-01", "Dra. Ana Rosero", Rol.MEDICO),
            "enf-01", new Profesional("enf-01", "Enf. Luis Benavides", Rol.ENFERMERA),
            "aud-01", new Profesional("aud-01", "Carmen Ortiz", Rol.AUDITOR)).get(id));

    final Historias historias = new Historias();
    final Bitacora bitacora = new Bitacora();
    final NotificadorFalso notificador = new NotificadorFalso();
    final Trazabilidad trazabilidad = new Trazabilidad(DIRECTORIO, bitacora, notificador, () -> AHORA);

    HistoriaClinica conPaciente(String documento) {
        var h = HistoriaClinica.abrir(documento, "María Guerrero", java.time.LocalDate.of(1980, 5, 12),
                DIRECTORIO.buscar("med-01").get(), AHORA);
        h.registrarAlergia(DIRECTORIO.buscar("med-01").get(), "Penicilina", Severidad.GRAVE, "Anafilaxia", AHORA);
        historias.datos.put(documento, h);
        return h;
    }
}
