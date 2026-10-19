package co.edu.ucc.clinica.historias.aplicacion.puertos.salida;

import java.util.Optional;

import co.edu.ucc.clinica.historias.dominio.Profesional;

/** Quién es el usuario y qué rol tiene. En producción vendría del sistema de identidad. */
public interface DirectorioDeProfesionales {
    Optional<Profesional> buscar(String profesionalId);
}
