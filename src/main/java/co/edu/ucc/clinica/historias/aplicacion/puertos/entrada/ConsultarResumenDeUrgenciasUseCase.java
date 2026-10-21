package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import co.edu.ucc.clinica.historias.dominio.ResumenDeUrgencias;

/** Alergias y antecedentes, rápido. */
public interface ConsultarResumenDeUrgenciasUseCase {
    ResumenDeUrgencias consultar(SolicitudDeConsulta solicitud);
}
