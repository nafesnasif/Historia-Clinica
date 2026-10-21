package co.edu.ucc.clinica.historias.aplicacion.puertos.salida;

import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;

/** Lectura y escritura. Lo usan los casos de uso que abren o modifican una historia. */
public interface HistoriaClinicaRepository extends LectorDeHistorias {
    void guardar(HistoriaClinica historia);
}
