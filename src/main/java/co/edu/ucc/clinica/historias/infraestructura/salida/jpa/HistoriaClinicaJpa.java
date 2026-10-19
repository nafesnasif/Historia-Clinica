package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "historia_clinica")
public class HistoriaClinicaJpa {
    @Id
    String documento;
    String nombre;
    LocalDate fechaNacimiento;
    String abiertaPor;
    Instant abiertaEn;

    protected HistoriaClinicaJpa() {
    }
}
