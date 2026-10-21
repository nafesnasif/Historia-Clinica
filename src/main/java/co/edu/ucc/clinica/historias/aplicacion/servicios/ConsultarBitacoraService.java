package co.edu.ucc.clinica.historias.aplicacion.servicios;

import java.util.List;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarBitacoraUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.ConsultaDeAccesos;
import co.edu.ucc.clinica.historias.dominio.PoliticaDeAcceso;
import co.edu.ucc.clinica.historias.dominio.Profesional;
import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;
import co.edu.ucc.clinica.historias.dominio.TipoAcceso;

public class ConsultarBitacoraService implements ConsultarBitacoraUseCase {
    private final ConsultaDeAccesos bitacora;
    private final IdentificacionDeProfesionales identificacion;
    private final RastroDeAccesos rastro;

    public ConsultarBitacoraService(ConsultaDeAccesos bitacora, IdentificacionDeProfesionales identificacion,
                                    RastroDeAccesos rastro) {
        this.bitacora = bitacora;
        this.identificacion = identificacion;
        this.rastro = rastro;
    }

    @Override
    public List<RegistroAcceso> consultar(Solicitud s) {
        Profesional p = identificacion.identificar(s, TipoAcceso.AUDITORIA);
        return rastro.registrandoNegativas(s.documento(), p, s.ambito(), TipoAcceso.AUDITORIA, () -> {
            PoliticaDeAcceso.autorizarAuditoria(p);
            List<RegistroAcceso> registros = bitacora.deHistoria(s.documento()); // se lee antes de anotar esta revisión
            rastro.permitido(s.documento(), p, s.ambito(), TipoAcceso.AUDITORIA, "Revisión de bitácora");
            return registros;
        });
    }
}
