package co.edu.ucc.clinica.historias.aplicacion.puertos.entrada;

import co.edu.ucc.clinica.historias.dominio.Ambito;
import co.edu.ucc.clinica.historias.dominio.EstadoAdministracion;
import co.edu.ucc.clinica.historias.dominio.Severidad;

public interface ActualizarHistoriaUseCase {
    record ComandoAlergia(String profesionalId, Ambito ambito, String documento, String sustancia,
                          Severidad severidad, String reaccion) {}
    record ComandoAntecedente(String profesionalId, Ambito ambito, String documento, String descripcion) {}
    record ComandoNota(String profesionalId, Ambito ambito, String documento, String texto) {}
    record ComandoPrescripcion(String profesionalId, Ambito ambito, String documento, String medicamento,
                               String dosis, String via, String frecuencia) {}
    record ComandoAdministracion(String profesionalId, Ambito ambito, String documento, String prescripcionId,
                                 EstadoAdministracion estado, String observacion) {}
    record ComandoEventoAdverso(String profesionalId, Ambito ambito, String documento, String prescripcionId,
                                String descripcion, Severidad severidad) {}

    String registrarAlergia(ComandoAlergia comando);         // devuelven el id de lo registrado
    String registrarAntecedente(ComandoAntecedente comando);
    String agregarNota(ComandoNota comando);
    String prescribir(ComandoPrescripcion comando);
    String registrarAdministracion(ComandoAdministracion comando);   // una fila del MAR
    String reportarEventoAdverso(ComandoEventoAdverso comando);
}
