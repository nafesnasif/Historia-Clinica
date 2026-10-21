package co.edu.ucc.clinica.historias.dominio;

/** Validaciones de datos obligatorios que se repetían en cada regla. */
final class Exigir {

    private Exigir() {
    }

    /** Devuelve el texto sin espacios a los lados, o lanza si viene vacío. */
    static String texto(String valor, String mensaje) {
        if (valor == null || valor.isBlank())
            throw new ReglaDeNegocioVioladaException(mensaje);
        return valor.trim();
    }

    static <T> T valor(T valor, String mensaje) {
        if (valor == null)
            throw new ReglaDeNegocioVioladaException(mensaje);
        return valor;
    }

    static String opcional(String valor) {
        return valor == null ? null : valor.trim();
    }
}
