package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import java.util.List;

import co.edu.ucc.clinica.historias.dominio.Ambito;
import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

public interface ConsultarBitacoraUseCase {
    record Solicitud(String profesionalId, Ambito ambito, String documento) implements OperacionSobreHistoria {}

    List<RegistroAcceso> consultar(Solicitud solicitud);
}
