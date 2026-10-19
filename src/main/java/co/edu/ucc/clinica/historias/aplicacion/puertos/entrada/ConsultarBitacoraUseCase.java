package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import java.util.List;

import co.edu.ucc.clinica.historias.dominio.Ambito;
import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

public interface ConsultarBitacoraUseCase {
    List<RegistroAcceso> consultar(String auditorId, Ambito ambito, String documento);
}
