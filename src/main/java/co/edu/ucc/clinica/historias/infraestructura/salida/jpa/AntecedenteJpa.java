package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "antecedente")
public class AntecedenteJpa {
    @Id
    String id;
    String historiaDocumento;
    @Column(length = 2000)
    String descripcion;
    String registradoPor;
    Instant registradoEn;

    protected AntecedenteJpa() {
    }
}
