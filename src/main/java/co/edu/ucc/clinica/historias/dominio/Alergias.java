package co.edu.ucc.clinica.historias.dominio;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Las alergias de una historia: registrarlas sin repetir y mostrarlas por severidad. */
final class Alergias {
    private final List<Alergia> lista = new ArrayList<>();

    Alergias(List<Alergia> iniciales) {
        lista.addAll(iniciales);
    }

    /** Regla: la misma sustancia no se registra dos veces. */
    Alergia registrar(Profesional quien, String sustancia, Severidad severidad, String reaccion, Instant ahora) {
        String nombre = Exigir.texto(sustancia, "La sustancia es obligatoria");
        Exigir.valor(severidad, "La severidad es obligatoria");
        if (buscar(nombre).isPresent())
            throw new ReglaDeNegocioVioladaException("La alergia a " + nombre + " ya está registrada");
        var alergia = new Alergia(UUID.randomUUID().toString(), nombre, severidad, reaccion, quien.id(), ahora);
        lista.add(alergia);
        return alergia;
    }

    /** La misma sustancia se compara sin distinguir mayúsculas. */
    private Optional<Alergia> buscar(String sustancia) {
        return lista.stream().filter(a -> a.sustancia().equalsIgnoreCase(sustancia)).findFirst();
    }

    /** Las graves primero: es lo primero que debe ver el médico de urgencias. */
    List<Alergia> deMayorAMenorSeveridad() {
        return lista.stream().sorted(Comparator.comparing(Alergia::severidad).reversed()).toList();
    }

    List<Alergia> todas() {
        return List.copyOf(lista);
    }
}
