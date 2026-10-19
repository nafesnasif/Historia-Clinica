package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.time.Instant;

import co.edu.ucc.clinica.historias.dominio.EstadoAdministracion;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "administracion_medicamento")
public class AdministracionJpa {
    @Id
    String id;
    String historiaDocumento;
    String prescripcionId;
    @Enumerated(EnumType.STRING)
    EstadoAdministracion estado;
    String observacion;
    String registradaPor;
    Instant registradaEn;

    protected AdministracionJpa() {
    }
}
