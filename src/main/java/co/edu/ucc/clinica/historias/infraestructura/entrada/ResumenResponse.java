package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.util.List;

import co.edu.ucc.clinica.historias.dominio.ResumenDeUrgencias;

public record ResumenResponse(String documento, String nombre, int edad, List<AlergiaDto> alergias,
                              List<AntecedenteDto> antecedentes) {
    public static ResumenResponse de(ResumenDeUrgencias r) {
        return new ResumenResponse(r.documentoPaciente(), r.nombrePaciente(), r.edad(),
                r.alergias().stream().map(AlergiaDto::de).toList(),
                r.antecedentes().stream().map(AntecedenteDto::de).toList());
    }
}
