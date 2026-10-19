package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroAccesoJpaRepo extends JpaRepository<RegistroAccesoJpa, String> {
    List<RegistroAccesoJpa> findByDocumentoPacienteOrderByOcurridoEn(String documentoPaciente);
}
