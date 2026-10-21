package co.edu.ucc.clinica.historias.dominio;

import static co.edu.ucc.clinica.historias.dominio.Permiso.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Cada rol declara sus permisos y el tipo de nota que escribe. Es el único lugar donde se cambia esto. */
public enum Rol {
    MEDICO(TipoNota.EVOLUCION_MEDICA, ABRIR_HISTORIA, VER_CONTENIDO_CLINICO, REGISTRAR_ALERGIA,
            REGISTRAR_ANTECEDENTE, ESCRIBIR_NOTA),
    ENFERMERA(TipoNota.NOTA_ENFERMERIA, ABRIR_HISTORIA, VER_CONTENIDO_CLINICO, REGISTRAR_ALERGIA,
            ESCRIBIR_NOTA),
    /** El auditor ve quién entró y cuándo, nunca el contenido clínico. */
    AUDITOR(null, VER_BITACORA);

    private final TipoNota tipoDeNota;
    private final Set<Permiso> permisos;

    Rol(TipoNota tipoDeNota, Permiso... permisos) {
        this.tipoDeNota = tipoDeNota;
        this.permisos = EnumSet.copyOf(List.of(permisos));
    }

    public boolean permite(Permiso permiso) { return permisos.contains(permiso); }

    /** Vacío para los roles que no escriben en la historia. */
    public Optional<TipoNota> tipoDeNota() { return Optional.ofNullable(tipoDeNota); }
}
