package co.edu.ucc.clinica.historias.infraestructura.salida.jpa;

import java.util.List;

import co.edu.ucc.clinica.historias.dominio.Alergia;
import co.edu.ucc.clinica.historias.dominio.Antecedente;
import co.edu.ucc.clinica.historias.dominio.HistoriaClinica;
import co.edu.ucc.clinica.historias.dominio.NotaClinica;
import co.edu.ucc.clinica.historias.dominio.RegistroAcceso;

/** Única responsabilidad: convertir entre las clases del dominio y las filas de la base de datos. */
final class MapeadorJpa {

    private MapeadorJpa() {
    }

    // ---- del dominio a filas ----

    static HistoriaClinicaJpa fila(HistoriaClinica h) {
        var f = new HistoriaClinicaJpa();
        f.documento = h.documentoPaciente();
        f.nombre = h.nombrePaciente();
        f.fechaNacimiento = h.fechaNacimiento();
        f.abiertaPor = h.abiertaPor();
        f.abiertaEn = h.abiertaEn();
        return f;
    }

    static AlergiaJpa filaDeAlergia(Alergia a, String documento) {
        var f = new AlergiaJpa();
        f.id = a.id();
        f.historiaDocumento = documento;
        f.sustancia = a.sustancia();
        f.severidad = a.severidad();
        f.reaccion = a.reaccion();
        f.registradaPor = a.registradaPor();
        f.registradaEn = a.registradaEn();
        return f;
    }

    static AntecedenteJpa filaDeAntecedente(Antecedente a, String documento) {
        var f = new AntecedenteJpa();
        f.id = a.id();
        f.historiaDocumento = documento;
        f.descripcion = a.descripcion();
        f.registradoPor = a.registradoPor();
        f.registradoEn = a.registradoEn();
        return f;
    }

    static NotaClinicaJpa filaDeNota(NotaClinica n, String documento) {
        var f = new NotaClinicaJpa();
        f.id = n.id();
        f.historiaDocumento = documento;
        f.tipo = n.tipo();
        f.texto = n.texto();
        f.autorId = n.autorId();
        f.escritaEn = n.escritaEn();
        return f;
    }

    static RegistroAccesoJpa filaDeRegistro(RegistroAcceso r) {
        var f = new RegistroAccesoJpa();
        f.id = r.id();
        f.documentoPaciente = r.documentoPaciente();
        f.profesionalId = r.profesionalId();
        f.rol = r.rol();
        f.ambito = r.ambito();
        f.tipo = r.tipo();
        f.resultado = r.resultado();
        f.detalle = r.detalle();
        f.ocurridoEn = r.ocurridoEn();
        return f;
    }

    // ---- de filas al dominio ----

    static HistoriaClinica historia(HistoriaClinicaJpa f, List<AlergiaJpa> alergias,
                                    List<AntecedenteJpa> antecedentes, List<NotaClinicaJpa> notas) {
        return HistoriaClinica.reconstruir(f.documento, f.nombre, f.fechaNacimiento, f.abiertaPor, f.abiertaEn,
                alergias.stream().map(MapeadorJpa::alergia).toList(),
                antecedentes.stream().map(MapeadorJpa::antecedente).toList(),
                notas.stream().map(MapeadorJpa::nota).toList());
    }

    static Alergia alergia(AlergiaJpa a) {
        return Alergia.reconstruir(a.id, a.sustancia, a.severidad, a.reaccion, a.registradaPor, a.registradaEn);
    }

    static Antecedente antecedente(AntecedenteJpa a) {
        return Antecedente.reconstruir(a.id, a.descripcion, a.registradoPor, a.registradoEn);
    }

    static NotaClinica nota(NotaClinicaJpa n) {
        return NotaClinica.reconstruir(n.id, n.tipo, n.texto, n.autorId, n.escritaEn);
    }

    static RegistroAcceso registro(RegistroAccesoJpa f) {
        return new RegistroAcceso(f.id, f.documentoPaciente, f.profesionalId, f.rol, f.ambito, f.tipo, f.resultado,
                f.detalle, f.ocurridoEn);
    }
}
