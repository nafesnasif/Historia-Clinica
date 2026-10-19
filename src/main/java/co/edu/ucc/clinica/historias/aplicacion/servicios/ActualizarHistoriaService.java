package co.edu.ucc.clinica.historias.aplicacion.servicios;

import java.time.Instant;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ActualizarHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.dominio.*;

public class ActualizarHistoriaService implements ActualizarHistoriaUseCase {
    private final HistoriaClinicaRepository historias;
    private final Trazabilidad trazabilidad;

    public ActualizarHistoriaService(HistoriaClinicaRepository historias, Trazabilidad trazabilidad) {
        this.historias = historias;
        this.trazabilidad = trazabilidad;
    }

    @Override
    public String registrarAlergia(ComandoAlergia c) {
        return modificar(c.profesionalId(), c.ambito(), c.documento(), "Alergia a " + c.sustancia(),
                (p, h) -> h.registrarAlergia(p, c.sustancia(), c.severidad(), c.reaccion(), ahora()).id());
    }

    @Override
    public String registrarAntecedente(ComandoAntecedente c) {
        return modificar(c.profesionalId(), c.ambito(), c.documento(), "Antecedente",
                (p, h) -> h.registrarAntecedente(p, c.descripcion(), ahora()).id());
    }

    @Override
    public String agregarNota(ComandoNota c) {
        return modificar(c.profesionalId(), c.ambito(), c.documento(), "Nota clínica",
                (p, h) -> h.agregarNota(p, c.texto(), ahora()).id());
    }

    @Override
    public String prescribir(ComandoPrescripcion c) {
        return modificar(c.profesionalId(), c.ambito(), c.documento(), "Prescripción de " + c.medicamento(),
                (p, h) -> h.prescribir(p, c.medicamento(), c.dosis(), c.via(), c.frecuencia(), ahora()).id());
    }

    @Override
    public String registrarAdministracion(ComandoAdministracion c) {
        return modificar(c.profesionalId(), c.ambito(), c.documento(),
                "MAR: dosis " + c.estado() + " de la prescripción " + c.prescripcionId(),
                (p, h) -> h.registrarAdministracion(p, c.prescripcionId(), c.estado(), c.observacion(), ahora()).id());
    }

    @Override
    public String reportarEventoAdverso(ComandoEventoAdverso c) {
        return modificar(c.profesionalId(), c.ambito(), c.documento(),
                "Evento adverso " + c.severidad() + " de la prescripción " + c.prescripcionId(),
                (p, h) -> h.reportarEventoAdverso(p, c.prescripcionId(), c.descripcion(), c.severidad(), ahora()).id());
    }

    interface Cambio { String aplicar(Profesional p, HistoriaClinica h); }

    /** Mismo guion para toda modificación: identificar, cambiar (decide el dominio), guardar y dejar rastro. */
    private String modificar(String profesionalId, Ambito ambito, String documento, String detalle,
                             Cambio cambio) {
        Profesional p = trazabilidad.identificar(profesionalId, documento, ambito, TipoAcceso.MODIFICACION);
        HistoriaClinica historia = historias.buscarPorDocumento(documento)
                .orElseThrow(() -> new HistoriaNoEncontradaException(documento));
        try {
            String id = cambio.aplicar(p, historia);
            historias.guardar(historia);
            trazabilidad.permitido(documento, p, ambito, TipoAcceso.MODIFICACION, detalle);
            return id;
        } catch (AccesoDenegadoException e) {
            trazabilidad.denegado(documento, p, ambito, TipoAcceso.MODIFICACION, e.getMessage());
            throw e;
        }
    }

    private Instant ahora() { return trazabilidad.reloj().ahora(); }
}
