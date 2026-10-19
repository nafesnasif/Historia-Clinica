package co.edu.ucc.clinica.historias.dominio;

import java.time.Instant;
import java.util.Objects;

/**
 * Una fila del MAR: qué pasó con una dosis de una prescripción. No se edita: un error se corrige
 * con un registro nuevo. Constructor de paquete: nace con HistoriaClinica.registrarAdministracion.
 */
public final class Administracion {
    private final String id;
    private final String prescripcionId;
    private final EstadoAdministracion estado;
    private final String observacion;
    private final String registradaPor;
    private final Instant registradaEn;

    Administracion(String id, String prescripcionId, EstadoAdministracion estado, String observacion,
                   String registradaPor, Instant registradaEn) {
        this.id = Objects.requireNonNull(id);
        this.prescripcionId = Objects.requireNonNull(prescripcionId);
        this.estado = Objects.requireNonNull(estado);
        this.observacion = observacion;
        this.registradaPor = Objects.requireNonNull(registradaPor);
        this.registradaEn = Objects.requireNonNull(registradaEn);
    }

    /** Solo para adaptadores que leen de una base de datos: no aplica
        reglas, los datos ya se validaron al registrarla. */
    public static Administracion reconstruir(String id, String prescripcionId, EstadoAdministracion estado,
                                             String observacion, String registradaPor, Instant registradaEn) {
        return new Administracion(id, prescripcionId, estado, observacion, registradaPor, registradaEn);
    }

    public String id() { return id; }
    public String prescripcionId() { return prescripcionId; }
    public EstadoAdministracion estado() { return estado; }
    public String observacion() { return observacion; }
    public String registradaPor() { return registradaPor; }
    public Instant registradaEn() { return registradaEn; }
}
