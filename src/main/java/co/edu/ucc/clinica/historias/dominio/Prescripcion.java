package co.edu.ucc.clinica.historias.dominio;

import java.time.Instant;
import java.util.Objects;

/**
 * Orden médica de un medicamento. Constructor de paquete: una prescripción nueva solo nace con
 * HistoriaClinica.prescribir, que verifica el rol y las alergias del paciente.
 */
public final class Prescripcion {
    private final String id;
    private final String medicamento;
    private final String dosis;
    private final String via;
    private final String frecuencia;
    private final String prescritaPor;
    private final Instant prescritaEn;

    Prescripcion(String id, String medicamento, String dosis, String via, String frecuencia,
                 String prescritaPor, Instant prescritaEn) {
        this.id = Objects.requireNonNull(id);
        this.medicamento = Objects.requireNonNull(medicamento);
        this.dosis = Objects.requireNonNull(dosis);
        this.via = Objects.requireNonNull(via);
        this.frecuencia = Objects.requireNonNull(frecuencia);
        this.prescritaPor = Objects.requireNonNull(prescritaPor);
        this.prescritaEn = Objects.requireNonNull(prescritaEn);
    }

    /** Solo para adaptadores que leen de una base de datos: no aplica
        reglas, los datos ya se validaron al prescribir. */
    public static Prescripcion reconstruir(String id, String medicamento, String dosis, String via,
                                           String frecuencia, String prescritaPor, Instant prescritaEn) {
        return new Prescripcion(id, medicamento, dosis, via, frecuencia, prescritaPor, prescritaEn);
    }

    public String id() { return id; }
    public String medicamento() { return medicamento; }
    public String dosis() { return dosis; }
    public String via() { return via; }
    public String frecuencia() { return frecuencia; }
    public String prescritaPor() { return prescritaPor; }
    public Instant prescritaEn() { return prescritaEn; }
}
