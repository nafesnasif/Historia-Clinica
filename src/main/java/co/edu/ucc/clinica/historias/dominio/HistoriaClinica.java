package co.edu.ucc.clinica.historias.dominio;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Raíz del agregado. Verifica los permisos de quien actúa y reparte el trabajo: las alergias
 * en {@link Alergias}. Antecedentes y notas
 * son listas de solo agregar y se quedan aquí.
 */
public class HistoriaClinica {
    /** Las fechas del negocio (edad, "hoy") son las del centro médico, no las de UTC. */
    public static final ZoneId ZONA_DEL_CENTRO = ZoneId.of("America/Bogota");

    private final String documentoPaciente;
    private final String nombrePaciente;
    private final LocalDate fechaNacimiento;
    private final String abiertaPor;
    private final Instant abiertaEn;
    private final Alergias alergias;
    private final List<Antecedente> antecedentes = new ArrayList<>();
    private final List<NotaClinica> notas = new ArrayList<>();

    private HistoriaClinica(String documento, String nombre, LocalDate fechaNacimiento, String abiertaPor,
                            Instant abiertaEn, List<Alergia> alergias, List<Antecedente> antecedentes,
                            List<NotaClinica> notas) {
        this.documentoPaciente = documento;
        this.nombrePaciente = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.abiertaPor = abiertaPor;
        this.abiertaEn = abiertaEn;
        this.alergias = new Alergias(alergias);
        this.antecedentes.addAll(antecedentes);
        this.notas.addAll(notas);
    }

    /** Punto único de creación: aquí viven las reglas. */
    public static HistoriaClinica abrir(String documento, String nombre, LocalDate fechaNacimiento,
                                        Profesional quien, Instant ahora) {
        quien.exigir(Permiso.ABRIR_HISTORIA);
        String elDocumento = Exigir.texto(documento, "El documento del paciente es obligatorio");
        String elNombre = Exigir.texto(nombre, "El nombre del paciente es obligatorio");
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.ofInstant(ahora, ZONA_DEL_CENTRO)))
            throw new ReglaDeNegocioVioladaException("La fecha de nacimiento no es válida");
        return new HistoriaClinica(elDocumento, elNombre, fechaNacimiento, quien.id(), ahora,
                List.of(), List.of(), List.of());
    }

    public Alergia registrarAlergia(Profesional quien, String sustancia, Severidad severidad,
                                    String reaccion, Instant ahora) {
        quien.exigir(Permiso.REGISTRAR_ALERGIA);
        return alergias.registrar(quien, sustancia, severidad, reaccion, ahora);
    }

    public Antecedente registrarAntecedente(Profesional quien, String descripcion, Instant ahora) {
        quien.exigir(Permiso.REGISTRAR_ANTECEDENTE);
        String texto = Exigir.texto(descripcion, "La descripción del antecedente es obligatoria");
        var antecedente = new Antecedente(UUID.randomUUID().toString(), texto, quien.id(), ahora);
        antecedentes.add(antecedente);
        return antecedente;
    }

    /** Regla: el tipo de nota lo define el rol de quien la escribe; nunca se edita una nota existente. */
    public NotaClinica agregarNota(Profesional quien, String texto, Instant ahora) {
        quien.exigir(Permiso.ESCRIBIR_NOTA);
        String contenido = Exigir.texto(texto, "La nota no puede estar vacía");
        TipoNota tipo = quien.rol().tipoDeNota().orElseThrow(() ->
                new AccesoDenegadoException("El rol " + quien.rol() + " no tiene un tipo de nota asignado"));
        var nota = new NotaClinica(UUID.randomUUID().toString(), tipo, contenido, quien.id(), ahora);
        notas.add(nota);
        return nota;
    }

    public ResumenDeUrgencias resumenDeUrgencias(Instant ahora) {
        return new ResumenDeUrgencias(documentoPaciente, nombrePaciente, edad(ahora),
                alergias.deMayorAMenorSeveridad(), List.copyOf(antecedentes));
    }

    public int edad(Instant ahora) {
        return Period.between(fechaNacimiento, LocalDate.ofInstant(ahora, ZONA_DEL_CENTRO)).getYears();
    }

    /** Solo para adaptadores que leen de una base de datos: no aplica
        reglas, los datos ya se validaron al crear. */
    public static HistoriaClinica reconstruir(String documento, String nombre, LocalDate fechaNacimiento,
                                              String abiertaPor, Instant abiertaEn,
                                              List<Alergia> alergias, List<Antecedente> antecedentes,
                                              List<NotaClinica> notas) {
        return new HistoriaClinica(documento, nombre, fechaNacimiento, abiertaPor, abiertaEn,
                alergias, antecedentes, notas);
    }

    public String documentoPaciente() { return documentoPaciente; }
    public String nombrePaciente() { return nombrePaciente; }
    public LocalDate fechaNacimiento() { return fechaNacimiento; }
    public String abiertaPor() { return abiertaPor; }
    public Instant abiertaEn() { return abiertaEn; }
    public List<Alergia> alergias() { return alergias.todas(); }
    public List<Antecedente> antecedentes() { return List.copyOf(antecedentes); }
    public List<NotaClinica> notas() { return List.copyOf(notas); }
}
