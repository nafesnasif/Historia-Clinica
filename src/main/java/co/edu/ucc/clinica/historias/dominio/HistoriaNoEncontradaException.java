package co.edu.ucc.clinica.historias.dominio;

public class HistoriaNoEncontradaException extends RuntimeException {
    public HistoriaNoEncontradaException(String documento) {
        super("No existe historia clínica para el documento " + documento);
    }
}
