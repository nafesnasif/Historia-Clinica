package co.edu.ucc.clinica.historias.dominio;

import java.time.Instant;
import java.util.Objects;

/**
 * Las notas no se editan ni se borran: una corrección es una nota nueva.
 * Constructor de paquete: una nota nueva solo nace con HistoriaClinica.agregarNota,
 * que decide el tipo según el rol de quien la escribe.
 */
public final class NotaClinica {
    private final String id;
    private final TipoNota tipo;
    private final String texto;
    private final String autorId;
    private final Instant escritaEn;

    NotaClinica(String id, TipoNota tipo, String texto, String autorId, Instant escritaEn) {
        this.id = Objects.requireNonNull(id);
        this.tipo = Objects.requireNonNull(tipo);
        this.texto = Objects.requireNonNull(texto);
        this.autorId = Objects.requireNonNull(autorId);
        this.escritaEn = Objects.requireNonNull(escritaEn);
    }

    /** Solo para adaptadores que leen de una base de datos: no aplica
        reglas, los datos ya se validaron al escribirla. */
    public static NotaClinica reconstruir(String id, TipoNota tipo, String texto, String autorId,
                                          Instant escritaEn) {
        return new NotaClinica(id, tipo, texto, autorId, escritaEn);
    }

    public String id() { return id; }
    public TipoNota tipo() { return tipo; }
    public String texto() { return texto; }
    public String autorId() { return autorId; }
    public Instant escritaEn() { return escritaEn; }
}
