package co.edu.ucc.clinica.historias.dominio;

import java.time.Instant;
import java.util.Objects;

/**
 * Reacción adversa a un medicamento ya administrado. Constructor de paquete: nace con
 * HistoriaClinica.reportarEventoAdverso, que además registra la alergia si el evento es grave.
 */
public final class EventoAdverso {
    private final String id;
    private final String prescripcionId;
    private final String medicamento;
    private final String descripcion;
    private final Severidad severidad;
    private final String reportadoPor;
    private final Instant reportadoEn;

    EventoAdverso(String id, String prescripcionId, String medicamento, String descripcion,
                  Severidad severidad, String reportadoPor, Instant reportadoEn) {
        this.id = Objects.requireNonNull(id);
        this.prescripcionId = Objects.requireNonNull(prescripcionId);
        this.medicamento = Objects.requireNonNull(medicamento);
        this.descripcion = Objects.requireNonNull(descripcion);
        this.severidad = Objects.requireNonNull(severidad);
        this.reportadoPor = Objects.requireNonNull(reportadoPor);
        this.reportadoEn = Objects.requireNonNull(reportadoEn);
    }

    /** Solo para adaptadores que leen de una base de datos: no aplica
        reglas, los datos ya se validaron al reportarlo. */
    public static EventoAdverso reconstruir(String id, String prescripcionId, String medicamento,
                                            String descripcion, Severidad severidad, String reportadoPor,
                                            Instant reportadoEn) {
        return new EventoAdverso(id, prescripcionId, medicamento, descripcion, severidad, reportadoPor,
                reportadoEn);
    }

    public String id() { return id; }
    public String prescripcionId() { return prescripcionId; }
    public String medicamento() { return medicamento; }
    public String descripcion() { return descripcion; }
    public Severidad severidad() { return severidad; }
    public String reportadoPor() { return reportadoPor; }
    public Instant reportadoEn() { return reportadoEn; }
}
