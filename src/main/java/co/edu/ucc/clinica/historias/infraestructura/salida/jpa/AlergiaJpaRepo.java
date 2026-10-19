package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlergiaJpaRepo extends JpaRepository<AlergiaJpa, String> {
    List<AlergiaJpa> findByHistoriaDocumentoOrderByRegistradaEn(String historiaDocumento);
}
