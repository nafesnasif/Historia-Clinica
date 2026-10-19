package co.edu.ucc.clinica.historias.aplicacion.servicios;

import java.util.Optional;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.BitacoraDeAccesos;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.DirectorioDeProfesionales;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Notificador;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Reloj;
import co.edu.ucc.clinica.historias.dominio.*;

/** Lo que comparten todos los casos de uso: saber quién es el usuario y dejar rastro de lo que hizo. */
public class Trazabilidad {
    private final DirectorioDeProfesionales directorio;
    private final BitacoraDeAccesos bitacora;
    private final Notificador notificador;
    private final Reloj reloj;

    public Trazabilidad(DirectorioDeProfesionales directorio, BitacoraDeAccesos bitacora,
                        Notificador notificador, Reloj reloj) {
        this.directorio = directorio;
        this.bitacora = bitacora;
        this.notificador = notificador;
        this.reloj = reloj;
    }

    /** Quien no está en el directorio no pasa, pero su intento queda en la bitácora para el auditor. */
    Profesional identificar(String profesionalId, String documento, Ambito ambito, TipoAcceso intento) {
        Optional<Profesional> profesional = profesionalId == null ? Optional.empty()
                : directorio.buscar(profesionalId);
        if (profesional.isEmpty()) {
            bitacora.registrar(RegistroAcceso.deDesconocido(documento, profesionalId, ambito, intento, reloj.ahora()));
            throw new AccesoDenegadoException("Profesional no reconocido: " + profesionalId);
        }
        return profesional.get();
    }

    void permitido(String documento, Profesional p, Ambito ambito, TipoAcceso tipo, String detalle) {
        var registro = RegistroAcceso.nuevo(documento, p, ambito, tipo, ResultadoAcceso.PERMITIDO,
                detalle, reloj.ahora());
        bitacora.registrar(registro);
        if (tipo == TipoAcceso.CONSULTA_URGENCIAS)
            notificador.accesoDeUrgencias(registro);
    }

    /** El intento rechazado también se registra: el auditor necesita verlo. */
    void denegado(String documento, Profesional p, Ambito ambito, TipoAcceso tipo, String razon) {
        bitacora.registrar(RegistroAcceso.nuevo(documento, p, ambito, tipo, ResultadoAcceso.DENEGADO,
                razon, reloj.ahora()));
    }

    Reloj reloj() { return reloj; }
}
