package co.edu.ucc.clinica.historias.aplicacion.puertos.salida;

import java.util.Optional;

import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;

public interface HistoriaClinicaRepository {
    void guardar(HistoriaClinica historia);
    Optional<HistoriaClinica> buscarPorDocumento(String documento);
    boolean existe(String documento);
}
