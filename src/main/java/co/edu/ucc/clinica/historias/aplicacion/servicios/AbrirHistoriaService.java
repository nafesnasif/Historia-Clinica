package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.AbrirHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.dominio.*;

public class AbrirHistoriaService implements AbrirHistoriaUseCase {
    private final HistoriaClinicaRepository historias;
    private final Trazabilidad trazabilidad;

    public AbrirHistoriaService(HistoriaClinicaRepository historias, Trazabilidad trazabilidad) {
        this.historias = historias;
        this.trazabilidad = trazabilidad;
    }

    @Override
    public String abrir(Comando c) {
        Profesional p = trazabilidad.identificar(c.profesionalId(), c.documento(), c.ambito(),
                TipoAcceso.APERTURA);
        try {
            var historia = HistoriaClinica.abrir(c.documento(), c.nombre(), c.fechaNacimiento(), p,
                    trazabilidad.reloj().ahora()); // decide el dominio
            // se verifica con el documento ya normalizado por el dominio: " 1085" y "1085" son el mismo paciente
            if (historias.existe(historia.documentoPaciente()))
                throw new ReglaDeNegocioVioladaException(
                        "Ya existe una historia para el documento " + historia.documentoPaciente());
            historias.guardar(historia);
            trazabilidad.permitido(historia.documentoPaciente(), p, c.ambito(), TipoAcceso.APERTURA,
                    "Apertura de historia");
            return historia.documentoPaciente();
        } catch (AccesoDenegadoException e) {
            trazabilidad.denegado(c.documento(), p, c.ambito(), TipoAcceso.APERTURA, e.getMessage());
            throw e;
        }
    }
}
