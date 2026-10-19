package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.dominio.Administracion;
import co.edu.ucc.clinica.historias.dominio.Alergia;
import co.edu.ucc.clinica.historias.dominio.Antecedente;
import co.edu.ucc.clinica.historias.dominio.EventoAdverso;
import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;
import co.edu.ucc.clinica.historias.dominio.NotaClinica;
import co.edu.ucc.clinica.historias.dominio.Prescripcion;

@Component
@Profile("postgres")
public class HistoriaClinicaRepositoryJpa implements HistoriaClinicaRepository {
    private final HistoriaClinicaJpaRepo historias;
    private final AlergiaJpaRepo alergias;
    private final AntecedenteJpaRepo antecedentes;
    private final NotaClinicaJpaRepo notas;
    private final PrescripcionJpaRepo prescripciones;
    private final AdministracionJpaRepo administraciones;
    private final EventoAdversoJpaRepo eventosAdversos;

    HistoriaClinicaRepositoryJpa(HistoriaClinicaJpaRepo historias, AlergiaJpaRepo alergias,
                                 AntecedenteJpaRepo antecedentes, NotaClinicaJpaRepo notas,
                                 PrescripcionJpaRepo prescripciones, AdministracionJpaRepo administraciones,
                                 EventoAdversoJpaRepo eventosAdversos) {
        this.historias = historias;
        this.alergias = alergias;
        this.antecedentes = antecedentes;
        this.notas = notas;
        this.prescripciones = prescripciones;
        this.administraciones = administraciones;
        this.eventosAdversos = eventosAdversos;
    }

    /** Solo inserta lo nuevo: lo ya escrito en una historia no se actualiza ni se borra. */
    @Override
    public void guardar(HistoriaClinica h) {
        if (!historias.existsById(h.documentoPaciente())) {
            var f = new HistoriaClinicaJpa();
            f.documento = h.documentoPaciente();
            f.nombre = h.nombrePaciente();
            f.fechaNacimiento = h.fechaNacimiento();
            f.abiertaPor = h.abiertaPor();
            f.abiertaEn = h.abiertaEn();
            historias.save(f);
        }
        for (Alergia a : h.alergias()) {
            if (alergias.existsById(a.id())) continue;
            var f = new AlergiaJpa();
            f.id = a.id();
            f.historiaDocumento = h.documentoPaciente();
            f.sustancia = a.sustancia();
            f.severidad = a.severidad();
            f.reaccion = a.reaccion();
            f.registradaPor = a.registradaPor();
            f.registradaEn = a.registradaEn();
            alergias.save(f);
        }
        for (Antecedente a : h.antecedentes()) {
            if (antecedentes.existsById(a.id())) continue;
            var f = new AntecedenteJpa();
            f.id = a.id();
            f.historiaDocumento = h.documentoPaciente();
            f.descripcion = a.descripcion();
            f.registradoPor = a.registradoPor();
            f.registradoEn = a.registradoEn();
            antecedentes.save(f);
        }
        for (NotaClinica n : h.notas()) {
            if (notas.existsById(n.id())) continue;
            var f = new NotaClinicaJpa();
            f.id = n.id();
            f.historiaDocumento = h.documentoPaciente();
            f.tipo = n.tipo();
            f.texto = n.texto();
            f.autorId = n.autorId();
            f.escritaEn = n.escritaEn();
            notas.save(f);
        }
        for (Prescripcion p : h.prescripciones()) {
            if (prescripciones.existsById(p.id())) continue;
            var f = new PrescripcionJpa();
            f.id = p.id();
            f.historiaDocumento = h.documentoPaciente();
            f.medicamento = p.medicamento();
            f.dosis = p.dosis();
            f.via = p.via();
            f.frecuencia = p.frecuencia();
            f.prescritaPor = p.prescritaPor();
            f.prescritaEn = p.prescritaEn();
            prescripciones.save(f);
        }
        for (Administracion a : h.administraciones()) {
            if (administraciones.existsById(a.id())) continue;
            var f = new AdministracionJpa();
            f.id = a.id();
            f.historiaDocumento = h.documentoPaciente();
            f.prescripcionId = a.prescripcionId();
            f.estado = a.estado();
            f.observacion = a.observacion();
            f.registradaPor = a.registradaPor();
            f.registradaEn = a.registradaEn();
            administraciones.save(f);
        }
        for (EventoAdverso e : h.eventosAdversos()) {
            if (eventosAdversos.existsById(e.id())) continue;
            var f = new EventoAdversoJpa();
            f.id = e.id();
            f.historiaDocumento = h.documentoPaciente();
            f.prescripcionId = e.prescripcionId();
            f.medicamento = e.medicamento();
            f.descripcion = e.descripcion();
            f.severidad = e.severidad();
            f.reportadoPor = e.reportadoPor();
            f.reportadoEn = e.reportadoEn();
            eventosAdversos.save(f);
        }
    }

    @Override
    public Optional<HistoriaClinica> buscarPorDocumento(String documento) {
        return historias.findById(documento).map(f -> HistoriaClinica.reconstruir(
                f.documento, f.nombre, f.fechaNacimiento, f.abiertaPor, f.abiertaEn,
                alergias.findByHistoriaDocumentoOrderByRegistradaEn(documento).stream()
                        .map(a -> Alergia.reconstruir(a.id, a.sustancia, a.severidad, a.reaccion, a.registradaPor,
                                a.registradaEn))
                        .toList(),
                antecedentes.findByHistoriaDocumentoOrderByRegistradoEn(documento).stream()
                        .map(a -> Antecedente.reconstruir(a.id, a.descripcion, a.registradoPor, a.registradoEn))
                        .toList(),
                notas.findByHistoriaDocumentoOrderByEscritaEn(documento).stream()
                        .map(n -> NotaClinica.reconstruir(n.id, n.tipo, n.texto, n.autorId, n.escritaEn))
                        .toList(),
                prescripciones.findByHistoriaDocumentoOrderByPrescritaEn(documento).stream()
                        .map(p -> Prescripcion.reconstruir(p.id, p.medicamento, p.dosis, p.via, p.frecuencia,
                                p.prescritaPor, p.prescritaEn))
                        .toList(),
                administraciones.findByHistoriaDocumentoOrderByRegistradaEn(documento).stream()
                        .map(a -> Administracion.reconstruir(a.id, a.prescripcionId, a.estado, a.observacion,
                                a.registradaPor, a.registradaEn))
                        .toList(),
                eventosAdversos.findByHistoriaDocumentoOrderByReportadoEn(documento).stream()
                        .map(e -> EventoAdverso.reconstruir(e.id, e.prescripcionId, e.medicamento, e.descripcion,
                                e.severidad, e.reportadoPor, e.reportadoEn))
                        .toList()));
    }

    @Override
    public boolean existe(String documento) {
        return historias.existsById(documento);
    }
}
