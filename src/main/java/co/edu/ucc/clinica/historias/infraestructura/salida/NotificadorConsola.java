package co.edu.ucc.clinica.historias.infraestructura.salida;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Notificador;
import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

@Component
@Profile("!correo")
class NotificadorConsola implements Notificador {

    @Override
    public void accesoDeUrgencias(RegistroAcceso r) {
        System.out.println("ALERTA al oficial de protección de datos: " + r.profesionalId() + " (" + r.rol()
                + ") abrió en urgencias la historia " + r.documentoPaciente() + " a las " + r.ocurridoEn()
                + ". Motivo: " + r.detalle());
    }
}
