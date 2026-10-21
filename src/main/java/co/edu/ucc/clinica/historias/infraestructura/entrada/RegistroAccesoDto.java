package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.time.Instant;

import co.edu.ucc.clinica.historias.dominio.Ambito;
import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;
import co.edu.ucc.clinica.historias.dominio.ResultadoAcceso;
import co.edu.ucc.clinica.historias.dominio.Rol;
import co.edu.ucc.clinica.historias.dominio.TipoAcceso;

/** Una línea de la bitácora tal como la ve el auditor. Mismos campos que antes devolvía el endpoint. */
public record RegistroAccesoDto(String id, String documentoPaciente, String profesionalId, Rol rol, Ambito ambito,
                                TipoAcceso tipo, ResultadoAcceso resultado, String detalle, Instant ocurridoEn) {
    public static RegistroAccesoDto de(RegistroAcceso r) {
        return new RegistroAccesoDto(r.id(), r.documentoPaciente(), r.profesionalId(), r.rol(), r.ambito(), r.tipo(),
                r.resultado(), r.detalle(), r.ocurridoEn());
    }
}
