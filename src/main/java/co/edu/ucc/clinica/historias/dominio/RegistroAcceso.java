package co.edu.ucc.clinica.historias.dominio;

import java.time.Instant;
import java.util.UUID;

/**
 * Una línea de la bitácora: quién, con qué rol, desde dónde, qué hizo, sobre qué historia y cuándo.
 * {@code rol} es null cuando quien lo intentó no está en el directorio de profesionales.
 */
public record RegistroAcceso(String id, String documentoPaciente, String profesionalId, Rol rol,
                             Ambito ambito, TipoAcceso tipo, ResultadoAcceso resultado,
                             String detalle, Instant ocurridoEn) {

    public static RegistroAcceso nuevo(String documentoPaciente, Profesional profesional, Ambito ambito,
                                       TipoAcceso tipo, ResultadoAcceso resultado, String detalle,
                                       Instant ahora) {
        return new RegistroAcceso(UUID.randomUUID().toString(), documentoPaciente, profesional.id(),
                profesional.rol(), ambito, tipo, resultado, detalle, ahora);
    }

    /** Regla: un intento de alguien que no es del centro siempre es denegado y siempre queda registrado. */
    public static RegistroAcceso deDesconocido(String documentoPaciente, String identificadorUsado, Ambito ambito,
                                               TipoAcceso tipo, Instant ahora) {
        String quien = identificadorUsado == null || identificadorUsado.isBlank() ? "(vacío)" : identificadorUsado;
        return new RegistroAcceso(UUID.randomUUID().toString(), documentoPaciente, quien, null, ambito, tipo,
                ResultadoAcceso.DENEGADO, "Profesional no reconocido", ahora);
    }

    public boolean esDeDesconocido() { return rol == null; }
}
