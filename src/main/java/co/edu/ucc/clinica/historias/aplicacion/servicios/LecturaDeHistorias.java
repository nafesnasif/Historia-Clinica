package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.SolicitudDeConsulta;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.LectorDeHistorias;
import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;
import co.edu.ucc.clinica.historias.dominio.HistoriaNoEncontradaException;
import co.edu.ucc.clinica.historias.dominio.PoliticaDeAcceso;
import co.edu.ucc.clinica.historias.dominio.Profesional;
import co.edu.ucc.clinica.historias.dominio.TipoAcceso;

/** El guion de toda lectura: identificar, autorizar antes de buscar, leer y dejar rastro. */
public class LecturaDeHistorias {
    private final LectorDeHistorias historias;
    private final IdentificacionDeProfesionales identificacion;
    private final RastroDeAccesos rastro;

    public LecturaDeHistorias(LectorDeHistorias historias, IdentificacionDeProfesionales identificacion,
                              RastroDeAccesos rastro) {
        this.historias = historias;
        this.identificacion = identificacion;
        this.rastro = rastro;
    }

    /** @param que lo que se leyó, para el detalle de la bitácora */
    public HistoriaClinica leer(SolicitudDeConsulta s, String que) {
        TipoAcceso intento = s.ambito().tipoDeConsulta();
        Profesional p = identificacion.identificar(s, intento);
        // Primero se autoriza: a quien no tiene permiso no se le dice ni siquiera si el paciente existe.
        TipoAcceso tipo = rastro.registrandoNegativas(s.documento(), p, s.ambito(), intento,
                () -> PoliticaDeAcceso.autorizarConsulta(p, s.ambito(), s.motivo()));
        HistoriaClinica historia = historias.buscarPorDocumento(s.documento())
                .orElseThrow(() -> new HistoriaNoEncontradaException(s.documento()));
        rastro.permitido(s.documento(), p, s.ambito(), tipo, que + ": " + motivoO(s.motivo()));
        return historia;
    }

    private static String motivoO(String motivo) {
        return motivo == null || motivo.isBlank() ? "sin motivo indicado" : motivo;
    }
}
