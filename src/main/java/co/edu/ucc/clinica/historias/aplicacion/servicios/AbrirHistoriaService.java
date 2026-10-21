package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.AbrirHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Reloj;
import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;
import co.edu.ucc.clinica.historias.dominio.Profesional;
import co.edu.ucc.clinica.historias.dominio.ReglaDeNegocioVioladaException;
import co.edu.ucc.clinica.historias.dominio.TipoAcceso;

public class AbrirHistoriaService implements AbrirHistoriaUseCase {
    private final HistoriaClinicaRepository historias;
    private final IdentificacionDeProfesionales identificacion;
    private final RastroDeAccesos rastro;
    private final Reloj reloj;

    public AbrirHistoriaService(HistoriaClinicaRepository historias, IdentificacionDeProfesionales identificacion,
                                RastroDeAccesos rastro, Reloj reloj) {
        this.historias = historias;
        this.identificacion = identificacion;
        this.rastro = rastro;
        this.reloj = reloj;
    }

    @Override
    public String abrir(Comando c) {
        Profesional p = identificacion.identificar(c, TipoAcceso.APERTURA);
        return rastro.registrandoNegativas(c.documento(), p, c.ambito(), TipoAcceso.APERTURA, () -> {
            var historia = HistoriaClinica.abrir(c.documento(), c.nombre(), c.fechaNacimiento(), p, reloj.ahora());
            // se verifica con el documento ya normalizado por el dominio: " 1085" y "1085" son el mismo paciente
            if (historias.existe(historia.documentoPaciente()))
                throw new ReglaDeNegocioVioladaException(
                        "Ya existe una historia para el documento " + historia.documentoPaciente());
            historias.guardar(historia);
            rastro.permitido(historia.documentoPaciente(), p, c.ambito(), TipoAcceso.APERTURA, "Apertura de historia");
            return historia.documentoPaciente();
        });
    }
}
