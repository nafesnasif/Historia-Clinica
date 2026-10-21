package co.edu.ucc.clinica.historias.aplicacion.puertos.salida;

import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

/** Escritura en la bitácora: solo se agrega, nunca se modifica ni se borra. */
public interface RegistroDeAccesos {
    void registrar(RegistroAcceso registro);
}
