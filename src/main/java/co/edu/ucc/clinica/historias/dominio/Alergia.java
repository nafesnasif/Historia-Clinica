package co.edu.ucc.clinica.historias.dominio;

import java.time.Instant;
import java.util.Objects;

/**
 * Constructor de paquete: una alergia nueva solo nace con HistoriaClinica.registrarAlergia,
 * que es donde se aplican las reglas (quién la registra, que no esté repetida).
 */
public final class Alergia {
    private final String id;
    private final String sustancia;
    private final Severidad severidad;
    private final String reaccion;
    private final String registradaPor;
    private final Instant registradaEn;

    Alergia(String id, String sustancia, Severidad severidad, String reaccion,
            String registradaPor, Instant registradaEn) {
        this.id = Objects.requireNonNull(id);
        this.sustancia = Objects.requireNonNull(sustancia);
        this.severidad = Objects.requireNonNull(severidad);
        this.reaccion = reaccion;
        this.registradaPor = Objects.requireNonNull(registradaPor);
        this.registradaEn = Objects.requireNonNull(registradaEn);
    }

    /** Solo para adaptadores que leen de una base de datos: no aplica
        reglas, los datos ya se validaron al registrarla. */
    public static Alergia reconstruir(String id, String sustancia, Severidad severidad, String reaccion,
                                      String registradaPor, Instant registradaEn) {
        return new Alergia(id, sustancia, severidad, reaccion, registradaPor, registradaEn);
    }

    public String id() { return id; }
    public String sustancia() { return sustancia; }
    public Severidad severidad() { return severidad; }
    public String reaccion() { return reaccion; }
    public String registradaPor() { return registradaPor; }
    public Instant registradaEn() { return registradaEn; }
}
