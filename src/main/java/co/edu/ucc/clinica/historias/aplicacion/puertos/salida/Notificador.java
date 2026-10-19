package co.edu.ucc.clinica.historias.aplicacion.puertos.salida;

import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

public interface Notificador {
    /** Avisa al oficial de protección de datos para que revise después el acceso de urgencias. */
    void accesoDeUrgencias(RegistroAcceso registro);
}
