package co.edu.ucc.clinica.historias.infraestructura.salida;

import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.DirectorioDeProfesionales;
import co.edu.ucc.clinica.historias.dominio.Profesional;
import co.edu.ucc.clinica.historias.dominio.Rol;

/** Reemplaza al sistema de identidad del centro médico (LDAP, Keycloak...) mientras no exista. */
@Component
@Profile("!identidad-remota")
class DirectorioDeProfesionalesFalso implements DirectorioDeProfesionales {

    private final Map<String, Profesional> profesionales = Map.of(
            "med-01", new Profesional("med-01", "Dra. Ana Rosero", Rol.MEDICO),
            "med-02", new Profesional("med-02", "Dr. Carlos Burbano", Rol.MEDICO),
            "enf-01", new Profesional("enf-01", "Enf. Luis Benavides", Rol.ENFERMERA),
            "aud-01", new Profesional("aud-01", "Carmen Ortiz", Rol.AUDITOR));

    @Override
    public Optional<Profesional> buscar(String profesionalId) {
        return Optional.ofNullable(profesionales.get(profesionalId));
    }
}
