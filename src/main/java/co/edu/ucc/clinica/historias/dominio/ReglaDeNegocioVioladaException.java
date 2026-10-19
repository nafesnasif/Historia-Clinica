package co.edu.ucc.clinica.historias.dominio;

public class ReglaDeNegocioVioladaException extends RuntimeException {
    public ReglaDeNegocioVioladaException(String mensaje) { super(mensaje); }
}
