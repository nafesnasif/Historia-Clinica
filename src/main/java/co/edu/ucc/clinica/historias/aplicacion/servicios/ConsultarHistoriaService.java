package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.SolicitudDeConsulta;
import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;

public class ConsultarHistoriaService implements ConsultarHistoriaUseCase {
    private final LecturaDeHistorias lectura;

    public ConsultarHistoriaService(LecturaDeHistorias lectura) {
        this.lectura = lectura;
    }

    @Override
    public HistoriaClinica consultar(SolicitudDeConsulta s) {
        return lectura.leer(s, "Historia completa");
    }
}
