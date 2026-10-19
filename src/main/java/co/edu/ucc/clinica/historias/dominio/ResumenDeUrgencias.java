package co.edu.ucc.clinica.historias.dominio;

import java.util.List;

/** Lo mínimo que urgencias necesita ver ya: alergias (las graves primero) y antecedentes. */
public record ResumenDeUrgencias(String documentoPaciente, String nombrePaciente, int edad,
                                 List<Alergia> alergias, List<Antecedente> antecedentes) {
}
