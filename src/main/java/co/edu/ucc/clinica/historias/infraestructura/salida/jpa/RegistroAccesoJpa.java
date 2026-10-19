package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.time.Instant;

import co.edu.ucc.clinica.historias.dominio.Ambito;
import co.edu.ucc.clinica.historias.dominio.ResultadoAcceso;
import co.edu.ucc.clinica.historias.dominio.Rol;
import co.edu.ucc.clinica.historias.dominio.TipoAcceso;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "registro_acceso")
public class RegistroAccesoJpa {
    @Id
    String id;
    String documentoPaciente;
    String profesionalId;
    @Enumerated(EnumType.STRING)
    Rol rol;
    @Enumerated(EnumType.STRING)
    Ambito ambito;
    @Enumerated(EnumType.STRING)
    TipoAcceso tipo;
    @Enumerated(EnumType.STRING)
    ResultadoAcceso resultado;
    @Column(length = 1000)
    String detalle;
    Instant ocurridoEn;

    protected RegistroAccesoJpa() {
    }
}
