package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarResumenDeUrgenciasUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.SolicitudDeConsulta;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Reloj;
import co.edu.ucc.clinica.historias.dominio.ResumenDeUrgencias;

public class ConsultarResumenDeUrgenciasService implements ConsultarResumenDeUrgenciasUseCase {
    private final LecturaDeHistorias lectura;
    private final Reloj reloj;

    public ConsultarResumenDeUrgenciasService(LecturaDeHistorias lectura, Reloj reloj) {
        this.lectura = lectura;
        this.reloj = reloj;
    }

    @Override
    public ResumenDeUrgencias consultar(SolicitudDeConsulta s) {
        return lectura.leer(s, "Resumen de urgencias").resumenDeUrgencias(reloj.ahora());
    }
}
