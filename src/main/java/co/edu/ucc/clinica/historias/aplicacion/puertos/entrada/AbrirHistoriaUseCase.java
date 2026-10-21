package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import java.time.LocalDate;

import co.edu.ucc.clinica.historias.dominio.Ambito;

public interface AbrirHistoriaUseCase {
    record Comando(String profesionalId, Ambito ambito, String documento, String nombre,
                   LocalDate fechaNacimiento) implements OperacionSobreHistoria {}

    String abrir(Comando comando); // devuelve el documento del paciente
}
