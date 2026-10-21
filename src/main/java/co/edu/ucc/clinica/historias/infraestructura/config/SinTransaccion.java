package co.edu.ucc.clinica.historias.infraestructura.config;

import java.util.function.Supplier;

/** Perfil memoria: no hay base de datos, cada escritura queda en el momento en que se hace. */
public class SinTransaccion implements EjecutorTransaccional {
    @Override
    public <T> T ejecutar(Supplier<T> trabajo) {
        return trabajo.get();
    }
}
