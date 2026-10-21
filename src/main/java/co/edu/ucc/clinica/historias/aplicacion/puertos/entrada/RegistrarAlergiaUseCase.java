package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import co.edu.ucc.clinica.historias.dominio.Ambito;
import co.edu.ucc.clinica.historias.dominio.Severidad;

public interface RegistrarAlergiaUseCase {
    record Comando(String profesionalId, Ambito ambito, String documento, String sustancia,
                   Severidad severidad, String reaccion) implements OperacionSobreHistoria {}

    String registrar(Comando comando); // devuelve el id de la alergia
}
