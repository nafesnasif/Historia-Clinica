package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.time.Instant;

import co.edu.ucc.clinica.historias.dominio.Alergia;
import co.edu.ucc.clinica.historias.dominio.Severidad;

public record AlergiaDto(String id, String sustancia, Severidad severidad, String reaccion, String registradaPor,
                         Instant registradaEn) {
    public static AlergiaDto de(Alergia a) {
        return new AlergiaDto(a.id(), a.sustancia(), a.severidad(), a.reaccion(), a.registradaPor(), a.registradaEn());
    }
}
