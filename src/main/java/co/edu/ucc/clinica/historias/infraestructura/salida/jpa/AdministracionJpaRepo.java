package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministracionJpaRepo extends JpaRepository<AdministracionJpa, String> {
    List<AdministracionJpa> findByHistoriaDocumentoOrderByRegistradaEn(String historiaDocumento);
}
