package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescripcionJpaRepo extends JpaRepository<PrescripcionJpa, String> {
    List<PrescripcionJpa> findByHistoriaDocumentoOrderByPrescritaEn(String historiaDocumento);
}
