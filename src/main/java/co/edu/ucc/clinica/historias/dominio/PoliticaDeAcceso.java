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
        if (!p.esAsistencial())
            throw new AccesoDenegadoException("El rol " + p.rol() + " no puede ver el contenido clínico");
        // Regla: en urgencias nunca se bloquea ni se exige motivo; se marca para revisión posterior.
        if (ambito == Ambito.URGENCIAS)
            return TipoAcceso.CONSULTA_URGENCIAS;
        if (motivo == null || motivo.isBlank())
            throw new AccesoDenegadoException("En consultorio se debe indicar el motivo de la consulta");
        return TipoAcceso.CONSULTA;
    }

    /** Regla: el auditor ve quién entró y cuándo, pero no el contenido clínico. */
    public static void autorizarAuditoria(Profesional p) {
        if (p.rol() != Rol.AUDITOR)
            throw new AccesoDenegadoException("Solo un auditor puede ver la bitácora de accesos");
    }
}
