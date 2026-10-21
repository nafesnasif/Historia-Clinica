package co.edu.ucc.clinica.historias.aplicacion.puertos.salida;

import java.util.Optional;

import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;

/** Lo que necesita quien solo consulta. Las consultas no reciben un objeto que también sabe guardar. */
public interface LectorDeHistorias {
    Optional<HistoriaClinica> buscarPorDocumento(String documento);
    boolean existe(String documento);
}
