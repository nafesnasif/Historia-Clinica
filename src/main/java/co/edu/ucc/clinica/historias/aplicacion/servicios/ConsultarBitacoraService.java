package co.edu.ucc.clinica.historias.aplicacion.servicios;

import java.util.List;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarBitacoraUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.BitacoraDeAccesos;
import co.edu.ucc.clinica.historias.dominio.*;

public class ConsultarBitacoraService implements ConsultarBitacoraUseCase {
    private final BitacoraDeAccesos bitacora;
    private final Trazabilidad trazabilidad;

    public ConsultarBitacoraService(BitacoraDeAccesos bitacora, Trazabilidad trazabilidad) {
        this.bitacora = bitacora;
        this.trazabilidad = trazabilidad;
    }

    @Override
    public List<RegistroAcceso> consultar(String auditorId, Ambito ambito, String documento) {
        Profesional p = trazabilidad.identificar(auditorId, documento, ambito, TipoAcceso.AUDITORIA);
        try {
            PoliticaDeAcceso.autorizarAuditoria(p);
            List<RegistroAcceso> registros = bitacora.deHistoria(documento); // se lee antes de anotar esta revisión
            trazabilidad.permitido(documento, p, ambito, TipoAcceso.AUDITORIA, "Revisión de bitácora");
            return registros;
        } catch (AccesoDenegadoException e) {
            trazabilidad.denegado(documento, p, ambito, TipoAcceso.AUDITORIA, e.getMessage());
            throw e;
        }
    }
}
