package co.edu.ucc.clinica.historias.dominio;

public record Profesional(String id, String nombre, Rol rol) {

    /** Médicos y enfermeras atienden pacientes; el auditor no. */
    public boolean esAsistencial() {
        return rol == Rol.MEDICO || rol == Rol.ENFERMERA;
    }
}
