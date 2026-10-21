package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;

public interface ConsultarHistoriaUseCase {
    HistoriaClinica consultar(SolicitudDeConsulta solicitud);
}
