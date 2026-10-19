package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.BitacoraDeAccesos;
import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

@Component
@Profile("postgres")
public class BitacoraJpa implements BitacoraDeAccesos {
    private final RegistroAccesoJpaRepo repo;
    BitacoraJpa(RegistroAccesoJpaRepo repo) { this.repo = repo; }

    @Override
    public void registrar(RegistroAcceso r) {
        var f = new RegistroAccesoJpa();
        f.id = r.id();
        f.documentoPaciente = r.documentoPaciente();
        f.profesionalId = r.profesionalId();
        f.rol = r.rol();
        f.ambito = r.ambito();
        f.tipo = r.tipo();
        f.resultado = r.resultado();
        f.detalle = r.detalle();
        f.ocurridoEn = r.ocurridoEn();
        repo.save(f);
    }

    @Override
    public List<RegistroAcceso> deHistoria(String documento) {
        return repo.findByDocumentoPacienteOrderByOcurridoEn(documento).stream()
                .map(f -> new RegistroAcceso(f.id, f.documentoPaciente, f.profesionalId, f.rol, f.ambito,
                        f.tipo, f.resultado, f.detalle, f.ocurridoEn))
                .toList();
    }
}
