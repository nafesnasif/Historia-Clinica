package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.time.Instant;

import co.edu.ucc.clinica.historias.dominio.TipoNota;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "nota_clinica")
public class NotaClinicaJpa {
    @Id
    String id;
    String historiaDocumento;
    @Enumerated(EnumType.STRING)
    TipoNota tipo;
    @Column(length = 4000)
    String texto;
    String autorId;
    Instant escritaEn;

    protected NotaClinicaJpa() {
    }
}
