package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.time.Instant;

import co.edu.ucc.clinica.historias.dominio.Antecedente;

public record AntecedenteDto(String id, String descripcion, String registradoPor, Instant registradoEn) {
    public static AntecedenteDto de(Antecedente a) {
        return new AntecedenteDto(a.id(), a.descripcion(), a.registradoPor(), a.registradoEn());
    }
}
