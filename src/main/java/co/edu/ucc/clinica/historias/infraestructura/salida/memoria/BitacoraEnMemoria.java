package co.edu.ucc.clinica.historias.infraestructura.salida.memoria;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.BitacoraDeAccesos;
import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

@Component
@Profile("memoria")
public class BitacoraEnMemoria implements BitacoraDeAccesos {
    private final List<RegistroAcceso> registros = new CopyOnWriteArrayList<>();

    @Override public void registrar(RegistroAcceso r) { registros.add(r); }

    @Override
    public List<RegistroAcceso> deHistoria(String documento) {
        return registros.stream()
                .filter(r -> r.documentoPaciente().equals(documento))
                .sorted(Comparator.comparing(RegistroAcceso::ocurridoEn))
                .toList();
    }
}
