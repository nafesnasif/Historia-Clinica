package co.edu.ucc.clinica.historias.dominio;

/**
 * Lo que un rol puede hacer sobre una historia. Las reglas del dominio preguntan por el permiso,
 * no por el rol: así un rol nuevo se agrega en {@link Rol} sin tocar HistoriaClinica ni la política de acceso.
 */
public enum Permiso {
    ABRIR_HISTORIA("abrir una historia"),
    VER_CONTENIDO_CLINICO("ver el contenido clínico"),
    REGISTRAR_ALERGIA("registrar alergias"),
    REGISTRAR_ANTECEDENTE("registrar antecedentes"),
    ESCRIBIR_NOTA("escribir notas"),
    VER_BITACORA("ver la bitácora de accesos");

    private final String descripcion;

    Permiso(String descripcion) { this.descripcion = descripcion; }

    /** Texto que completa el mensaje "El rol X no puede ...". */
    public String descripcion() { return descripcion; }
}
