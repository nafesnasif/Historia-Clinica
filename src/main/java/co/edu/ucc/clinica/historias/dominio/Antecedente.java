package co.edu.ucc.clinica.historias.dominio;

import java.time.Instant;
import java.util.Objects;

/** Constructor de paquete: un antecedente nuevo solo nace con HistoriaClinica.registrarAntecedente. */
public final class Antecedente {
    private final String id;
    private final String descripcion;
    private final String registradoPor;
    private final Instant registradoEn;

    Antecedente(String id, String descripcion, String registradoPor, Instant registradoEn) {
        this.id = Objects.requireNonNull(id);
        this.descripcion = Objects.requireNonNull(descripcion);
        this.registradoPor = Objects.requireNonNull(registradoPor);
        this.registradoEn = Objects.requireNonNull(registradoEn);
    }

    /** Solo para adaptadores que leen de una base de datos: no aplica
        reglas, los datos ya se validaron al registrarlo. */
    public static Antecedente reconstruir(String id, String descripcion, String registradoPor,
                                          Instant registradoEn) {
        return new Antecedente(id, descripcion, registradoPor, registradoEn);
    }

    public String id() { return id; }
    public String descripcion() { return descripcion; }
    public String registradoPor() { return registradoPor; }
    public Instant registradoEn() { return registradoEn; }
}
