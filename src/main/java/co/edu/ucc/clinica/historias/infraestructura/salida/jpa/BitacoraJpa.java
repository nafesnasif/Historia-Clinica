package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.BitacoraDeAccesos;
import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

@Component
@Profile("mariadb")
public class BitacoraJpa implements BitacoraDeAccesos {
    private final RegistroAccesoJpaRepo repo;

    BitacoraJpa(RegistroAccesoJpaRepo repo) {
        this.repo = repo;
    }

    @Override
    public void registrar(RegistroAcceso r) {
        repo.save(MapeadorJpa.filaDeRegistro(r));
    }

    @Override
    public List<RegistroAcceso> deHistoria(String documento) {
        return repo.findByDocumentoPacienteOrderByOcurridoEn(documento).stream().map(MapeadorJpa::registro).toList();
    }
}
