package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.AgregarNotaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Reloj;

public class AgregarNotaService implements AgregarNotaUseCase {
    private final ModificadorDeHistorias modificador;
    private final Reloj reloj;

    public AgregarNotaService(ModificadorDeHistorias modificador, Reloj reloj) {
        this.modificador = modificador;
        this.reloj = reloj;
    }

    @Override
    public String agregar(Comando c) {
        return modificador.modificar(c, "Nota clínica",
                (p, h) -> h.agregarNota(p, c.texto(), reloj.ahora()).id());
    }
}
