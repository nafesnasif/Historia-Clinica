package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.dominio.Alergia;
import co.edu.ucc.clinica.historias.dominio.Antecedente;
import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;
import co.edu.ucc.clinica.historias.dominio.NotaClinica;

/** Lee y escribe historias en MariaDB. La conversión de datos la hace {@link MapeadorJpa}. */
@Component
@Profile("mariadb")
public class HistoriaClinicaRepositoryJpa implements HistoriaClinicaRepository {
    private final HistoriaClinicaJpaRepo historias;
    private final AlergiaJpaRepo alergias;
    private final AntecedenteJpaRepo antecedentes;
    private final NotaClinicaJpaRepo notas;

    HistoriaClinicaRepositoryJpa(HistoriaClinicaJpaRepo historias, AlergiaJpaRepo alergias,
                                 AntecedenteJpaRepo antecedentes, NotaClinicaJpaRepo notas) {
        this.historias = historias;
        this.alergias = alergias;
        this.antecedentes = antecedentes;
        this.notas = notas;
    }

    /** Solo inserta lo nuevo: lo ya escrito en una historia no se actualiza ni se borra. */
    @Override
    public void guardar(HistoriaClinica h) {
        String doc = h.documentoPaciente();
        if (!historias.existsById(doc))
            historias.save(MapeadorJpa.fila(h));
        insertarNuevos(h.alergias(), Alergia::id, alergias, a -> MapeadorJpa.filaDeAlergia(a, doc));
        insertarNuevos(h.antecedentes(), Antecedente::id, antecedentes, a -> MapeadorJpa.filaDeAntecedente(a, doc));
        insertarNuevos(h.notas(), NotaClinica::id, notas, n -> MapeadorJpa.filaDeNota(n, doc));
    }

    @Override
    public Optional<HistoriaClinica> buscarPorDocumento(String documento) {
        return historias.findById(documento).map(f -> MapeadorJpa.historia(f,
                alergias.findByHistoriaDocumentoOrderByRegistradaEn(documento),
                antecedentes.findByHistoriaDocumentoOrderByRegistradoEn(documento),
                notas.findByHistoriaDocumentoOrderByEscritaEn(documento)));
    }

    @Override
    public boolean existe(String documento) {
        return historias.existsById(documento);
    }

    private static <T, F> void insertarNuevos(List<T> elementos, Function<T, String> id,
                                              JpaRepository<F, String> repo, Function<T, F> aFila) {
        for (T elemento : elementos)
            if (!repo.existsById(id.apply(elemento)))
                repo.save(aFila.apply(elemento));
    }
}
