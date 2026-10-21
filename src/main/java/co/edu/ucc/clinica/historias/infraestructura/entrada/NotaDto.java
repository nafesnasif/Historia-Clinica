package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.time.Instant;

import co.edu.ucc.clinica.historias.dominio.NotaClinica;
import co.edu.ucc.clinica.historias.dominio.TipoNota;

public record NotaDto(String id, TipoNota tipo, String texto, String autorId, Instant escritaEn) {
    public static NotaDto de(NotaClinica n) {
        return new NotaDto(n.id(), n.tipo(), n.texto(), n.autorId(), n.escritaEn());
    }
}
