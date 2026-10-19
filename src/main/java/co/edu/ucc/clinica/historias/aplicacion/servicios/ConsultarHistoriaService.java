package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.dominio.*;

public class ConsultarHistoriaService implements ConsultarHistoriaUseCase {
    private final HistoriaClinicaRepository historias;
    private final Trazabilidad trazabilidad;

    public ConsultarHistoriaService(HistoriaClinicaRepository historias, Trazabilidad trazabilidad) {
        this.historias = historias;
        this.trazabilidad = trazabilidad;
    }

    @Override
    public HistoriaClinica consultar(Solicitud s) {
        return autorizarYLeer(s, "Historia completa");
    }

    @Override
    public ResumenDeUrgencias resumenDeUrgencias(Solicitud s) {
        return autorizarYLeer(s, "Resumen de urgencias").resumenDeUrgencias(trazabilidad.reloj().ahora());
    }

    private HistoriaClinica autorizarYLeer(Solicitud s, String que) {
        var intento = s.ambito() == Ambito.URGENCIAS ? TipoAcceso.CONSULTA_URGENCIAS : TipoAcceso.CONSULTA;
        Profesional p = trazabilidad.identificar(s.profesionalId(), s.documento(), s.ambito(), intento);
        // Primero se autoriza: a quien no tiene permiso no se le dice ni siquiera si el paciente existe.
        TipoAcceso tipo;
        try {
            tipo = PoliticaDeAcceso.autorizarConsulta(p, s.ambito(), s.motivo());
        } catch (AccesoDenegadoException e) {
            trazabilidad.denegado(s.documento(), p, s.ambito(), intento, e.getMessage());
            throw e;
        }
        HistoriaClinica historia = historias.buscarPorDocumento(s.documento())
                .orElseThrow(() -> new HistoriaNoEncontradaException(s.documento()));
        trazabilidad.permitido(s.documento(), p, s.ambito(), tipo, que + ": " + motivoO(s.motivo()));
        return historia;
    }

    private static String motivoO(String motivo) {
        return motivo == null || motivo.isBlank() ? "sin motivo indicado" : motivo;
    }
}
