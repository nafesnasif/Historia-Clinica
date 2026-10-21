package co.edu.ucc.clinica.historias.infraestructura.config;

import java.util.function.Supplier;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import co.edu.ucc.clinica.historias.dominio.AccesoDenegadoException;

/**
 * El cambio en la historia y su línea de bitácora se guardan juntos o no se guarda ninguno.
 * Un acceso denegado no cambió nada, pero su registro en la bitácora sí debe quedar:
 * por eso se confirma la transacción y luego se relanza la excepción.
 */
public class TransaccionConBitacora implements EjecutorTransaccional {
    private final TransactionTemplate tx;

    public TransaccionConBitacora(PlatformTransactionManager transacciones) {
        this.tx = new TransactionTemplate(transacciones);
    }

    @Override
    public <T> T ejecutar(Supplier<T> trabajo) {
        var denegado = new AccesoDenegadoException[1];
        T resultado = tx.execute(estado -> {
            try {
                return trabajo.get();
            } catch (AccesoDenegadoException e) {
                denegado[0] = e;
                return null;
            }
        });
        if (denegado[0] != null)
            throw denegado[0];
        return resultado;
    }
}
