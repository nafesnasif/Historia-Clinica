package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import co.edu.ucc.clinica.historias.dominio.Ambito;

public interface AgregarNotaUseCase {
    record Comando(String profesionalId, Ambito ambito, String documento, String texto)
            implements OperacionSobreHistoria {}

    String agregar(Comando comando); // devuelve el id de la nota
}
