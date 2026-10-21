package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.RegistrarAntecedenteUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Reloj;

public class RegistrarAntecedenteService implements RegistrarAntecedenteUseCase {
    private final ModificadorDeHistorias modificador;
    private final Reloj reloj;

    public RegistrarAntecedenteService(ModificadorDeHistorias modificador, Reloj reloj) {
        this.modificador = modificador;
        this.reloj = reloj;
    }

    @Override
    public String registrar(Comando c) {
        return modificador.modificar(c, "Antecedente",
                (p, h) -> h.registrarAntecedente(p, c.descripcion(), reloj.ahora()).id());
    }
}
