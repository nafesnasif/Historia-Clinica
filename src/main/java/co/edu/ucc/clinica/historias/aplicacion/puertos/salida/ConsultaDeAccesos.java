package co.edu.ucc.clinica.historias.aplicacion.puertos.salida;

import java.util.List;

import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

/** Lectura de la bitácora. Solo la usa la auditoría. */
public interface ConsultaDeAccesos {
    List<RegistroAcceso> deHistoria(String documento); // en orden cronológico
}
