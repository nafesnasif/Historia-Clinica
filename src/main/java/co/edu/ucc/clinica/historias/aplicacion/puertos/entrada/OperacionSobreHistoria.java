package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import co.edu.ucc.clinica.historias.dominio.Ambito;

/** Lo que traen todos los comandos y solicitudes: quién actúa, desde dónde y sobre qué historia. */
public interface OperacionSobreHistoria {
    String profesionalId();
    Ambito ambito();
    String documento();
}
