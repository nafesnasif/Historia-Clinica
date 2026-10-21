package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import co.edu.ucc.clinica.historias.dominio.Ambito;

public interface RegistrarAntecedenteUseCase {
    record Comando(String profesionalId, Ambito ambito, String documento, String descripcion)
            implements OperacionSobreHistoria {}

    String registrar(Comando comando); // devuelve el id del antecedente
}
