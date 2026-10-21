package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;

public record HistoriaResponse(String documento, String nombre, LocalDate fechaNacimiento, String abiertaPor,
                               Instant abiertaEn, List<AlergiaDto> alergias, List<AntecedenteDto> antecedentes,
                               List<NotaDto> notas) {
    public static HistoriaResponse de(HistoriaClinica h) {
        return new HistoriaResponse(h.documentoPaciente(), h.nombrePaciente(), h.fechaNacimiento(),
                h.abiertaPor(), h.abiertaEn(), h.alergias().stream().map(AlergiaDto::de).toList(),
                h.antecedentes().stream().map(AntecedenteDto::de).toList(),
                h.notas().stream().map(NotaDto::de).toList());
    }
}
