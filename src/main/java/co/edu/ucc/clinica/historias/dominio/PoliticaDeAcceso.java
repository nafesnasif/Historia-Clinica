package co.edu.ucc.clinica.historias.dominio;

/**
 * Reglas de quién puede ver una historia y su bitácora.
 * Tensión del negocio: en urgencias la vida del paciente pesa más que el trámite,
 * pero ningún acceso queda sin rastro (habeas data).
 */
public final class PoliticaDeAcceso {

    private PoliticaDeAcceso() {
    }

    /** Devuelve cómo debe quedar marcado el acceso en la bitácora, o lanza si no se permite. */
    public static TipoAcceso autorizarConsulta(Profesional p, Ambito ambito, String motivo) {
        p.exigir(Permiso.VER_CONTENIDO_CLINICO);
        if (ambito.exigeMotivo() && (motivo == null || motivo.isBlank()))
            throw new AccesoDenegadoException("En " + ambito.nombre() + " se debe indicar el motivo de la consulta");
        return ambito.tipoDeConsulta();
    }

    /** Regla: el auditor ve quién entró y cuándo, pero no el contenido clínico. */
    public static void autorizarAuditoria(Profesional p) {
        p.exigir(Permiso.VER_BITACORA);
    }
}
