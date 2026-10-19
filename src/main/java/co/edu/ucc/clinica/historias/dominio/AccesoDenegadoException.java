package co.edu.ucc.clinica.historias.dominio;

/** Habeas data: quien no tiene derecho a ver o modificar la historia no pasa. */
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) { super(mensaje); }
}
