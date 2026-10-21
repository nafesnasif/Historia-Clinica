package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.OperacionSobreHistoria;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;
import co.edu.ucc.clinica.historias.dominio.HistoriaNoEncontradaException;
import co.edu.ucc.clinica.historias.dominio.Profesional;
import co.edu.ucc.clinica.historias.dominio.TipoAcceso;

/**
 * El mismo guion para toda modificación: identificar, cambiar (decide el dominio), guardar y dejar rastro.
 * Cada caso de uso de modificación solo aporta su {@link Cambio}.
 */
public class ModificadorDeHistorias {
    private final HistoriaClinicaRepository historias;
    private final IdentificacionDeProfesionales identificacion;
    private final RastroDeAccesos rastro;

    public ModificadorDeHistorias(HistoriaClinicaRepository historias, IdentificacionDeProfesionales identificacion,
                                  RastroDeAccesos rastro) {
        this.historias = historias;
        this.identificacion = identificacion;
        this.rastro = rastro;
    }

    @FunctionalInterface
    public interface Cambio {
        /** Aplica el cambio sobre la historia y devuelve el id de lo registrado. */
        String aplicar(Profesional quien, HistoriaClinica historia);
    }

    public String modificar(OperacionSobreHistoria operacion, String detalle, Cambio cambio) {
        Profesional p = identificacion.identificar(operacion, TipoAcceso.MODIFICACION);
        String documento = operacion.documento();
        HistoriaClinica historia = historias.buscarPorDocumento(documento)
                .orElseThrow(() -> new HistoriaNoEncontradaException(documento));
        return rastro.registrandoNegativas(documento, p, operacion.ambito(), TipoAcceso.MODIFICACION, () -> {
            String id = cambio.aplicar(p, historia);
            historias.guardar(historia);
            rastro.permitido(documento, p, operacion.ambito(), TipoAcceso.MODIFICACION, detalle);
            return id;
        });
    }
}
