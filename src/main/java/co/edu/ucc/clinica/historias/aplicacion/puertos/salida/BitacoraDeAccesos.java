package co.edu.ucc.clinica.historias.aplicacion.puertos.salida;

import java.util.List;

import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

/** Solo se agrega y se lee: la bitácora nunca se modifica ni se borra. */
public interface BitacoraDeAccesos {
    void registrar(RegistroAcceso registro);
    List<RegistroAcceso> deHistoria(String documento); // en orden cronológico
}
