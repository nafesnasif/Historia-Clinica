package co.edu.ucc.clinica.historias.aplicacion.servicios;

import java.util.Optional;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.OperacionSobreHistoria;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.DirectorioDeProfesionales;
import co.edu.ucc.clinica.historias.dominio.AccesoDenegadoException;
import co.edu.ucc.clinica.historias.dominio.Profesional;
import co.edu.ucc.clinica.historias.dominio.TipoAcceso;

/** Única responsabilidad: saber quién es el usuario. */
public class IdentificacionDeProfesionales {
    private final DirectorioDeProfesionales directorio;
    private final RastroDeAccesos rastro;

    public IdentificacionDeProfesionales(DirectorioDeProfesionales directorio, RastroDeAccesos rastro) {
        this.directorio = directorio;
        this.rastro = rastro;
    }

    /** Quien no está en el directorio no pasa, pero su intento queda en la bitácora para el auditor. */
    public Profesional identificar(OperacionSobreHistoria operacion, TipoAcceso intento) {
        String id = operacion.profesionalId();
        Optional<Profesional> profesional = id == null ? Optional.empty() : directorio.buscar(id);
        if (profesional.isEmpty()) {
            rastro.desconocido(operacion.documento(), id, operacion.ambito(), intento);
            throw new AccesoDenegadoException("Profesional no reconocido: " + id);
        }
        return profesional.get();
    }
}
