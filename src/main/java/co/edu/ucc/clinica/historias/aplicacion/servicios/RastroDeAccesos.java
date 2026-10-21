package co.edu.ucc.clinica.historias.aplicacion.servicios;

import java.util.List;
import java.util.function.Supplier;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.RegistroDeAccesos;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Reloj;
import co.edu.ucc.clinica.historias.dominio.AccesoDenegadoException;
import co.edu.ucc.clinica.historias.dominio.Ambito;
import co.edu.ucc.clinica.historias.dominio.Profesional;
import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;
import co.edu.ucc.clinica.historias.dominio.ResultadoAcceso;
import co.edu.ucc.clinica.historias.dominio.TipoAcceso;

/** Única responsabilidad: escribir en la bitácora lo que pasó y avisar a los oyentes. */
public class RastroDeAccesos {
    private final RegistroDeAccesos bitacora;
    private final List<OyenteDeAccesos> oyentes;
    private final Reloj reloj;

    public RastroDeAccesos(RegistroDeAccesos bitacora, List<OyenteDeAccesos> oyentes, Reloj reloj) {
        this.bitacora = bitacora;
        this.oyentes = List.copyOf(oyentes);
        this.reloj = reloj;
    }

    public void permitido(String documento, Profesional p, Ambito ambito, TipoAcceso tipo, String detalle) {
        anotar(RegistroAcceso.nuevo(documento, p, ambito, tipo, ResultadoAcceso.PERMITIDO, detalle, reloj.ahora()));
    }

    /** El intento rechazado también se registra: el auditor necesita verlo. */
    public void denegado(String documento, Profesional p, Ambito ambito, TipoAcceso tipo, String razon) {
        anotar(RegistroAcceso.nuevo(documento, p, ambito, tipo, ResultadoAcceso.DENEGADO, razon, reloj.ahora()));
    }

    /** Alguien que no está en el directorio de profesionales intentó entrar. */
    public void desconocido(String documento, String identificadorUsado, Ambito ambito, TipoAcceso intento) {
        anotar(RegistroAcceso.deDesconocido(documento, identificadorUsado, ambito, intento, reloj.ahora()));
    }

    /**
     * Ejecuta la acción. Si el dominio la niega, deja el intento en la bitácora y relanza la excepción.
     * Antes cada servicio repetía este try/catch.
     */
    public <T> T registrandoNegativas(String documento, Profesional p, Ambito ambito, TipoAcceso tipo,
                                      Supplier<T> accion) {
        try {
            return accion.get();
        } catch (AccesoDenegadoException e) {
            denegado(documento, p, ambito, tipo, e.getMessage());
            throw e;
        }
    }

    private void anotar(RegistroAcceso registro) {
        bitacora.registrar(registro);
        oyentes.forEach(o -> o.registrado(registro));
    }
}
