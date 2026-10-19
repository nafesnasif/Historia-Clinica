package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoAdversoJpaRepo extends JpaRepository<EventoAdversoJpa, String> {
    List<EventoAdversoJpa> findByHistoriaDocumentoOrderByReportadoEn(String historiaDocumento);
}
