package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AntecedenteJpaRepo extends JpaRepository<AntecedenteJpa, String> {
    List<AntecedenteJpa> findByHistoriaDocumentoOrderByRegistradoEn(String historiaDocumento);
}
