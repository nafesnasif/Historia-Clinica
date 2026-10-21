package co.edu.ucc.clinica.historias.aplicacion.servicios;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.RegistrarAlergiaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.Reloj;

public class RegistrarAlergiaService implements RegistrarAlergiaUseCase {
    private final ModificadorDeHistorias modificador;
    private final Reloj reloj;

    public RegistrarAlergiaService(ModificadorDeHistorias modificador, Reloj reloj) {
        this.modificador = modificador;
        this.reloj = reloj;
    }

    @Override
    public String registrar(Comando c) {
        return modificador.modificar(c, "Alergia a " + c.sustancia(),
                (p, h) -> h.registrarAlergia(p, c.sustancia(), c.severidad(), c.reaccion(), reloj.ahora()).id());
    }
}
