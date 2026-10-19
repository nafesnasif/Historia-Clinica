package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaClinicaJpaRepo extends JpaRepository<NotaClinicaJpa, String> {
    List<NotaClinicaJpa> findByHistoriaDocumentoOrderByEscritaEn(String historiaDocumento);
}
