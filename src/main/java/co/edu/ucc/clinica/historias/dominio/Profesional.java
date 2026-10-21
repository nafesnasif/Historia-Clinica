package co.edu.ucc.clinica.historias.dominio;

public record Profesional(String id, String nombre, Rol rol) {

    public boolean puede(Permiso permiso) {
        return rol != null && rol.permite(permiso);
    }

    /** Lanza AccesoDenegadoException si el rol no tiene el permiso. */
    public void exigir(Permiso permiso) {
        if (!puede(permiso))
            throw new AccesoDenegadoException("El rol " + rol + " no puede " + permiso.descripcion());
    }
}
