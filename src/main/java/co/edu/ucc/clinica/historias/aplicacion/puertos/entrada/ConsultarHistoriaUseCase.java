package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import co.edu.ucc.clinica.historias.dominio.Ambito;
import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;
import co.edu.ucc.clinica.historias.dominio.ResumenDeUrgencias;

public interface ConsultarHistoriaUseCase {
    record Solicitud(String profesionalId, Ambito ambito, String documento, String motivo) {}
    HistoriaClinica consultar(Solicitud solicitud);
    ResumenDeUrgencias resumenDeUrgencias(Solicitud solicitud); // alergias y antecedentes, rápido
}
