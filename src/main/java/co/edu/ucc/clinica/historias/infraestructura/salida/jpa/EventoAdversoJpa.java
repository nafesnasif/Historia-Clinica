package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.time.Instant;

import co.edu.ucc.clinica.historias.dominio.Severidad;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "evento_adverso")
public class EventoAdversoJpa {
    @Id
    String id;
    String historiaDocumento;
    String prescripcionId;
    String medicamento;
    String descripcion;
    @Enumerated(EnumType.STRING)
    Severidad severidad;
    String reportadoPor;
    Instant reportadoEn;

    protected EventoAdversoJpa() {
    }
}
