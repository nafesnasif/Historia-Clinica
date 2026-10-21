package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import co.edu.ucc.clinica.historias.dominio.Ambito;

/** La comparten la consulta completa y el resumen de urgencias. */
public record SolicitudDeConsulta(String profesionalId, Ambito ambito, String documento, String motivo)
        implements OperacionSobreHistoria {}
