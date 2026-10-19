package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriaClinicaJpaRepo extends JpaRepository<HistoriaClinicaJpa, String> {
}
