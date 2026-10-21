package co.edu.ucc.clinica.historias.dominio;

/**
 * Desde dónde se consulta la historia. Cada ámbito trae su regla: si exige motivo y cómo queda marcado
 * el acceso en la bitácora. Un ámbito nuevo (hospitalización, por ejemplo) se agrega aquí y
 * PoliticaDeAcceso no cambia.
 */
public enum Ambito {
    CONSULTORIO("consultorio", true, TipoAcceso.CONSULTA),
    /** En urgencias nunca se bloquea ni se exige motivo; el acceso queda marcado para revisión posterior. */
    URGENCIAS("urgencias", false, TipoAcceso.CONSULTA_URGENCIAS);

    private final String nombre;
    private final boolean exigeMotivo;
    private final TipoAcceso tipoDeConsulta;

    Ambito(String nombre, boolean exigeMotivo, TipoAcceso tipoDeConsulta) {
        this.nombre = nombre;
        this.exigeMotivo = exigeMotivo;
        this.tipoDeConsulta = tipoDeConsulta;
    }

    public String nombre() { return nombre; }
    public boolean exigeMotivo() { return exigeMotivo; }
    public TipoAcceso tipoDeConsulta() { return tipoDeConsulta; }
}
