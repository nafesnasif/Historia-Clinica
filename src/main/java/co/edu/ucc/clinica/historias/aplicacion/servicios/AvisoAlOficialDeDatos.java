package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Notificador;
import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;
import co.edu.ucc.clinica.historias.dominio.ResultadoAcceso;

/** Cada acceso permitido que requiere revisión posterior (los de urgencias) se avisa al oficial de protección de datos. */
public class AvisoAlOficialDeDatos implements OyenteDeAccesos {
    private final Notificador notificador;

    public AvisoAlOficialDeDatos(Notificador notificador) {
        this.notificador = notificador;
    }

    @Override
    public void registrado(RegistroAcceso registro) {
        if (registro.resultado() == ResultadoAcceso.PERMITIDO && registro.tipo().requiereRevisionPosterior())
            notificador.accesoDeUrgencias(registro);
    }
}
