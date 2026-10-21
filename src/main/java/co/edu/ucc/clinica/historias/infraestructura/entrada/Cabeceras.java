package co.edu.ucc.clinica.historias.infraestructura.entrada;

/**
 * Quién llama (X-Profesional) y desde dónde (X-Ambito) llegan en cabeceras para simplificar el ejemplo.
 * En producción saldrían del token de sesión y de la red o terminal desde la que se conecta.
 */
final class Cabeceras {
    static final String PROFESIONAL = "X-Profesional";
    static final String AMBITO = "X-Ambito";

    private Cabeceras() {
    }
}
