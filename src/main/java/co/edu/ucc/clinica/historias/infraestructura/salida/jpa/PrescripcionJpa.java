package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "prescripcion")
public class PrescripcionJpa {
    @Id
    String id;
    String historiaDocumento;
    String medicamento;
    String dosis;
    String via;
    String frecuencia;
    String prescritaPor;
    Instant prescritaEn;

    protected PrescripcionJpa() {
    }
}
