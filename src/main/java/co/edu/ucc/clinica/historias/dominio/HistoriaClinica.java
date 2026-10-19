package co.edu.ucc.clinica.historias.dominio;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HistoriaClinica {
    /** Las fechas del negocio (edad, "hoy") son las del centro médico, no las de UTC. */
    public static final ZoneId ZONA_DEL_CENTRO = ZoneId.of("America/Bogota");

    private final String documentoPaciente;
    private final String nombrePaciente;
    private final LocalDate fechaNacimiento;
    private final String abiertaPor;
    private final Instant abiertaEn;
    private final List<Alergia> alergias = new ArrayList<>();
    private final List<Antecedente> antecedentes = new ArrayList<>();
    private final List<NotaClinica> notas = new ArrayList<>();
    private final List<Prescripcion> prescripciones = new ArrayList<>();
    private final List<Administracion> administraciones = new ArrayList<>();
    private final List<EventoAdverso> eventosAdversos = new ArrayList<>();

    private HistoriaClinica(String documento, String nombre, LocalDate fechaNacimiento,
                            String abiertaPor, Instant abiertaEn) {
        this.documentoPaciente = documento;
        this.nombrePaciente = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.abiertaPor = abiertaPor;
        this.abiertaEn = abiertaEn;
    }

    /** Punto único de creación: aquí viven las reglas. */
    public static HistoriaClinica abrir(String documento, String nombre, LocalDate fechaNacimiento,
                                        Profesional quien, Instant ahora) {
        exigirAsistencial(quien, "abrir una historia");
        if (documento == null || documento.isBlank())
            throw new ReglaDeNegocioVioladaException("El documento del paciente es obligatorio");
        if (nombre == null || nombre.isBlank())
            throw new ReglaDeNegocioVioladaException("El nombre del paciente es obligatorio");
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.ofInstant(ahora, ZONA_DEL_CENTRO)))
            throw new ReglaDeNegocioVioladaException("La fecha de nacimiento no es válida");
        return new HistoriaClinica(documento.trim(), nombre.trim(), fechaNacimiento, quien.id(), ahora);
    }

    /** Regla: médicos y enfermeras registran alergias; la misma sustancia no se registra dos veces. */
    public Alergia registrarAlergia(Profesional quien, String sustancia, Severidad severidad,
                                    String reaccion, Instant ahora) {
        exigirAsistencial(quien, "registrar alergias");
        if (sustancia == null || sustancia.isBlank())
            throw new ReglaDeNegocioVioladaException("La sustancia es obligatoria");
        if (severidad == null)
            throw new ReglaDeNegocioVioladaException("La severidad es obligatoria");
        boolean repetida = alergias.stream().anyMatch(a -> a.sustancia().equalsIgnoreCase(sustancia.trim()));
        if (repetida)
            throw new ReglaDeNegocioVioladaException("La alergia a " + sustancia.trim() + " ya está registrada");
        var alergia = new Alergia(UUID.randomUUID().toString(), sustancia.trim(), severidad, reaccion,
                quien.id(), ahora);
        alergias.add(alergia);
        return alergia;
    }

    /** Regla: los antecedentes los registra solo un médico. */
    public Antecedente registrarAntecedente(Profesional quien, String descripcion, Instant ahora) {
        if (quien.rol() != Rol.MEDICO)
            throw new AccesoDenegadoException("Solo un médico puede registrar antecedentes");
        if (descripcion == null || descripcion.isBlank())
            throw new ReglaDeNegocioVioladaException("La descripción del antecedente es obligatoria");
        var antecedente = new Antecedente(UUID.randomUUID().toString(), descripcion.trim(), quien.id(), ahora);
        antecedentes.add(antecedente);
        return antecedente;
    }

    /** Regla: el tipo de nota lo define el rol de quien la escribe; nunca se edita una nota existente. */
    public NotaClinica agregarNota(Profesional quien, String texto, Instant ahora) {
        exigirAsistencial(quien, "escribir notas");
        if (texto == null || texto.isBlank())
            throw new ReglaDeNegocioVioladaException("La nota no puede estar vacía");
        var tipo = quien.rol() == Rol.MEDICO ? TipoNota.EVOLUCION_MEDICA : TipoNota.NOTA_ENFERMERIA;
        var nota = new NotaClinica(UUID.randomUUID().toString(), tipo, texto.trim(), quien.id(), ahora);
        notas.add(nota);
        return nota;
    }

    /** Regla: solo un médico prescribe, y nunca un medicamento al que el paciente es alérgico. */
    public Prescripcion prescribir(Profesional quien, String medicamento, String dosis, String via,
                                   String frecuencia, Instant ahora) {
        if (quien.rol() != Rol.MEDICO)
            throw new AccesoDenegadoException("Solo un médico puede prescribir medicamentos");
        if (medicamento == null || medicamento.isBlank())
            throw new ReglaDeNegocioVioladaException("El medicamento es obligatorio");
        if (dosis == null || dosis.isBlank() || via == null || via.isBlank()
                || frecuencia == null || frecuencia.isBlank())
            throw new ReglaDeNegocioVioladaException("La dosis, la vía y la frecuencia son obligatorias");
        exigirQueNoSeaAlergico(medicamento.trim(), "prescribir");
        var prescripcion = new Prescripcion(UUID.randomUUID().toString(), medicamento.trim(), dosis.trim(),
                via.trim(), frecuencia.trim(), quien.id(), ahora);
        prescripciones.add(prescripcion);
        return prescripcion;
    }

    /**
     * MAR: médicos y enfermeras registran qué pasó con cada dosis. Omitir o rechazar exige el motivo,
     * y no se administra un medicamento al que el paciente se volvió alérgico después de la orden.
     */
    public Administracion registrarAdministracion(Profesional quien, String prescripcionId,
                                                  EstadoAdministracion estado, String observacion,
                                                  Instant ahora) {
        exigirAsistencial(quien, "registrar la administración de medicamentos");
        var prescripcion = prescripcion(prescripcionId);
        if (estado == null)
            throw new ReglaDeNegocioVioladaException("El estado de la administración es obligatorio");
        if (estado != EstadoAdministracion.ADMINISTRADA && (observacion == null || observacion.isBlank()))
            throw new ReglaDeNegocioVioladaException("Una dosis " + estado + " exige el motivo en la observación");
        if (estado == EstadoAdministracion.ADMINISTRADA)
            exigirQueNoSeaAlergico(prescripcion.medicamento(), "administrar");
        var administracion = new Administracion(UUID.randomUUID().toString(), prescripcion.id(), estado,
                observacion == null ? null : observacion.trim(), quien.id(), ahora);
        administraciones.add(administracion);
        return administracion;
    }

    /**
     * Solo se reporta un evento adverso de un medicamento que sí se administró. Si es grave, queda
     * registrada la alergia: así ninguna orden ni dosis futura de ese medicamento pasa.
     */
    public EventoAdverso reportarEventoAdverso(Profesional quien, String prescripcionId, String descripcion,
                                               Severidad severidad, Instant ahora) {
        exigirAsistencial(quien, "reportar eventos adversos");
        var prescripcion = prescripcion(prescripcionId);
        if (descripcion == null || descripcion.isBlank())
            throw new ReglaDeNegocioVioladaException("La descripción del evento adverso es obligatoria");
        if (severidad == null)
            throw new ReglaDeNegocioVioladaException("La severidad es obligatoria");
        boolean administrada = administraciones.stream().anyMatch(a ->
                a.prescripcionId().equals(prescripcion.id()) && a.estado() == EstadoAdministracion.ADMINISTRADA);
        if (!administrada)
            throw new ReglaDeNegocioVioladaException(
                    "No hay dosis administradas de " + prescripcion.medicamento() + " para reportar un evento adverso");
        var evento = new EventoAdverso(UUID.randomUUID().toString(), prescripcion.id(), prescripcion.medicamento(),
                descripcion.trim(), severidad, quien.id(), ahora);
        eventosAdversos.add(evento);
        if (severidad == Severidad.GRAVE && alergiaA(prescripcion.medicamento()).isEmpty())
            alergias.add(new Alergia(UUID.randomUUID().toString(), prescripcion.medicamento(), Severidad.GRAVE,
                    descripcion.trim(), quien.id(), ahora));
        return evento;
    }

    private Prescripcion prescripcion(String prescripcionId) {
        return prescripciones.stream().filter(p -> p.id().equals(prescripcionId)).findFirst()
                .orElseThrow(() -> new ReglaDeNegocioVioladaException(
                        "La prescripción " + prescripcionId + " no existe en esta historia"));
    }

    /** Compara por nombre exacto: no reconoce familias (penicilina / amoxicilina). */
    private Optional<Alergia> alergiaA(String medicamento) {
        return alergias.stream().filter(a -> a.sustancia().equalsIgnoreCase(medicamento)).findFirst();
    }

    private void exigirQueNoSeaAlergico(String medicamento, String accion) {
        alergiaA(medicamento).ifPresent(a -> {
            throw new ReglaDeNegocioVioladaException("No se puede " + accion + " " + medicamento
                    + ": el paciente es alérgico (" + a.severidad() + ")");
        });
    }

    /** Alergias graves primero: es lo primero que debe ver el médico de urgencias. */
    public ResumenDeUrgencias resumenDeUrgencias(Instant ahora) {
        var alergiasOrdenadas = alergias.stream()
                .sorted(Comparator.comparing(Alergia::severidad).reversed())
                .toList();
        return new ResumenDeUrgencias(documentoPaciente, nombrePaciente, edad(ahora),
                alergiasOrdenadas, List.copyOf(antecedentes));
    }

    public int edad(Instant ahora) {
        return Period.between(fechaNacimiento, LocalDate.ofInstant(ahora, ZONA_DEL_CENTRO)).getYears();
    }

    private static void exigirAsistencial(Profesional quien, String accion) {
        if (!quien.esAsistencial())
            throw new AccesoDenegadoException("El rol " + quien.rol() + " no puede " + accion);
    }

    /** Solo para adaptadores que leen de una base de datos: no aplica
        reglas, los datos ya se validaron al crear. */
    public static HistoriaClinica reconstruir(String documento, String nombre, LocalDate fechaNacimiento,
                                              String abiertaPor, Instant abiertaEn,
                                              List<Alergia> alergias, List<Antecedente> antecedentes,
                                              List<NotaClinica> notas) {
        return reconstruir(documento, nombre, fechaNacimiento, abiertaPor, abiertaEn, alergias, antecedentes,
                notas, List.of(), List.of(), List.of());
    }

    /** Igual que el anterior, incluyendo la medicación (MAR) y los eventos adversos. */
    public static HistoriaClinica reconstruir(String documento, String nombre, LocalDate fechaNacimiento,
                                              String abiertaPor, Instant abiertaEn,
                                              List<Alergia> alergias, List<Antecedente> antecedentes,
                                              List<NotaClinica> notas, List<Prescripcion> prescripciones,
                                              List<Administracion> administraciones,
                                              List<EventoAdverso> eventosAdversos) {
        var h = new HistoriaClinica(documento, nombre, fechaNacimiento, abiertaPor, abiertaEn);
        h.alergias.addAll(alergias);
        h.antecedentes.addAll(antecedentes);
        h.notas.addAll(notas);
        h.prescripciones.addAll(prescripciones);
        h.administraciones.addAll(administraciones);
        h.eventosAdversos.addAll(eventosAdversos);
        return h;
    }

    public String documentoPaciente() { return documentoPaciente; }
    public String nombrePaciente() { return nombrePaciente; }
    public LocalDate fechaNacimiento() { return fechaNacimiento; }
    public String abiertaPor() { return abiertaPor; }
    public Instant abiertaEn() { return abiertaEn; }
    public List<Alergia> alergias() { return List.copyOf(alergias); }
    public List<Antecedente> antecedentes() { return List.copyOf(antecedentes); }
    public List<NotaClinica> notas() { return List.copyOf(notas); }
    public List<Prescripcion> prescripciones() { return List.copyOf(prescripciones); }
    public List<Administracion> administraciones() { return List.copyOf(administraciones); }
    public List<EventoAdverso> eventosAdversos() { return List.copyOf(eventosAdversos); }
}
