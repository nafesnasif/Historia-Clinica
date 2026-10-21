package co.edu.ucc.clinica.historias.infraestructura.config;

import java.util.function.Supplier;

/**
 * Ejecuta un caso de uso según la infraestructura disponible: directamente en memoria o dentro de una
 * transacción de base de datos. Conserva el resultado y las excepciones de la operación.
 */
public interface EjecutorTransaccional {
    <T> T ejecutar(Supplier<T> trabajo);
}
