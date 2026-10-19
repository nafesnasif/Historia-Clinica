package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.time.Instant;

import co.edu.ucc.clinica.historias.dominio.Severidad;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "alergia")
public class AlergiaJpa {
    @Id
    String id;
    String historiaDocumento;
    String sustancia;
    @Enumerated(EnumType.STRING)
    Severidad severidad;
    String reaccion;
    String registradaPor;
    Instant registradaEn;

    protected AlergiaJpa() {
    }
}
