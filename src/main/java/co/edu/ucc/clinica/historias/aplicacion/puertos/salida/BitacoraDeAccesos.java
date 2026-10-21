package co.edu.ucc.clinica.historias.aplicacion.puertos.salida;

/**
 * La bitácora completa, para los adaptadores (memoria, JPA). Cada servicio pide solo la mitad que usa:
 * {@link RegistroDeAccesos} para escribir o {@link ConsultaDeAccesos} para leer.
 */
public interface BitacoraDeAccesos extends RegistroDeAccesos, ConsultaDeAccesos {
}
