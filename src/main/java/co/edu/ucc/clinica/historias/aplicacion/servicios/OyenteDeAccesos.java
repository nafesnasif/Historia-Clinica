package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

/**
 * Reacciona a cada línea que entra a la bitácora. Para agregar una reacción nueva (una alerta,
 * una métrica) se escribe otra implementación y se registra en la configuración; RastroDeAccesos no cambia.
 */
@FunctionalInterface
public interface OyenteDeAccesos {
    void registrado(RegistroAcceso registro);
}
