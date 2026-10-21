package co.edu.ucc.clinica.historias.dominio;

public enum TipoAcceso {
    APERTURA(false),
    CONSULTA(false),
    CONSULTA_URGENCIAS(true),
    MODIFICACION(false),
    AUDITORIA(false);

    private final boolean requiereRevisionPosterior;

    TipoAcceso(boolean requiereRevisionPosterior) { this.requiereRevisionPosterior = requiereRevisionPosterior; }

    /** El oficial de protección de datos revisa después los accesos de este tipo. */
    public boolean requiereRevisionPosterior() { return requiereRevisionPosterior; }
}
