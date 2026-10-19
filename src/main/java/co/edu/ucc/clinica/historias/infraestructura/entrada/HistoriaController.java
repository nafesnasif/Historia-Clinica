package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.AbrirHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ActualizarHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarBitacoraUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarHistoriaUseCase;
import co.edu.ucc.clinica.historias.dominio.*;

/**
 * Quién llama (X-Profesional) y desde dónde (X-Ambito) llegan en cabeceras para simplificar el ejemplo.
 * En producción saldrían del token de sesión y de la red o terminal desde la que se conecta.
 */
@RestController
@RequestMapping("/historias")
class HistoriaController {
    private final AbrirHistoriaUseCase abrir;
    private final ConsultarHistoriaUseCase consultar;
    private final ActualizarHistoriaUseCase actualizar;
    private final ConsultarBitacoraUseCase bitacora;

    HistoriaController(AbrirHistoriaUseCase abrir, ConsultarHistoriaUseCase consultar,
                       ActualizarHistoriaUseCase actualizar, ConsultarBitacoraUseCase bitacora) {
        this.abrir = abrir;
        this.consultar = consultar;
        this.actualizar = actualizar;
        this.bitacora = bitacora;
    }

    record AperturaRequest(String documento, String nombre, LocalDate fechaNacimiento) {}
    record AlergiaRequest(String sustancia, Severidad severidad, String reaccion) {}
    record AntecedenteRequest(String descripcion) {}
    record NotaRequest(String texto) {}
    record PrescripcionRequest(String medicamento, String dosis, String via, String frecuencia) {}
    record AdministracionRequest(EstadoAdministracion estado, String observacion) {}
    record EventoAdversoRequest(String descripcion, Severidad severidad) {}
    // Respuestas: el API publica su propio formato, no las clases del dominio.
    record AlergiaDto(String id, String sustancia, Severidad severidad, String reaccion, String registradaPor,
                      Instant registradaEn) {
        static AlergiaDto de(Alergia a) {
            return new AlergiaDto(a.id(), a.sustancia(), a.severidad(), a.reaccion(), a.registradaPor(),
                    a.registradaEn());
        }
    }
    record AntecedenteDto(String id, String descripcion, String registradoPor, Instant registradoEn) {
        static AntecedenteDto de(Antecedente a) {
            return new AntecedenteDto(a.id(), a.descripcion(), a.registradoPor(), a.registradoEn());
        }
    }
    record NotaDto(String id, TipoNota tipo, String texto, String autorId, Instant escritaEn) {
        static NotaDto de(NotaClinica n) {
            return new NotaDto(n.id(), n.tipo(), n.texto(), n.autorId(), n.escritaEn());
        }
    }
    record AdministracionDto(String id, EstadoAdministracion estado, String observacion, String registradaPor,
                             Instant registradaEn) {
        static AdministracionDto de(Administracion a) {
            return new AdministracionDto(a.id(), a.estado(), a.observacion(), a.registradaPor(), a.registradaEn());
        }
    }
    record EventoAdversoDto(String id, String descripcion, Severidad severidad, String reportadoPor,
                            Instant reportadoEn) {
        static EventoAdversoDto de(EventoAdverso e) {
            return new EventoAdversoDto(e.id(), e.descripcion(), e.severidad(), e.reportadoPor(), e.reportadoEn());
        }
    }
    /** Una línea del MAR: la orden con sus dosis y sus eventos adversos. */
    record PrescripcionDto(String id, String medicamento, String dosis, String via, String frecuencia,
                           String prescritaPor, Instant prescritaEn, List<AdministracionDto> administraciones,
                           List<EventoAdversoDto> eventosAdversos) {
        static PrescripcionDto de(Prescripcion p, HistoriaClinica h) {
            return new PrescripcionDto(p.id(), p.medicamento(), p.dosis(), p.via(), p.frecuencia(),
                    p.prescritaPor(), p.prescritaEn(),
                    h.administraciones().stream().filter(a -> a.prescripcionId().equals(p.id()))
                            .map(AdministracionDto::de).toList(),
                    h.eventosAdversos().stream().filter(e -> e.prescripcionId().equals(p.id()))
                            .map(EventoAdversoDto::de).toList());
        }
    }
    record HistoriaResponse(String documento, String nombre, LocalDate fechaNacimiento, String abiertaPor,
                            Instant abiertaEn, List<AlergiaDto> alergias, List<AntecedenteDto> antecedentes,
                            List<NotaDto> notas, List<PrescripcionDto> medicacion) {
        static HistoriaResponse de(HistoriaClinica h) {
            return new HistoriaResponse(h.documentoPaciente(), h.nombrePaciente(), h.fechaNacimiento(),
                    h.abiertaPor(), h.abiertaEn(), h.alergias().stream().map(AlergiaDto::de).toList(),
                    h.antecedentes().stream().map(AntecedenteDto::de).toList(),
                    h.notas().stream().map(NotaDto::de).toList(),
                    h.prescripciones().stream().map(p -> PrescripcionDto.de(p, h)).toList());
        }
    }
    record ResumenResponse(String documento, String nombre, int edad, List<AlergiaDto> alergias,
                           List<AntecedenteDto> antecedentes) {
        static ResumenResponse de(ResumenDeUrgencias r) {
            return new ResumenResponse(r.documentoPaciente(), r.nombrePaciente(), r.edad(),
                    r.alergias().stream().map(AlergiaDto::de).toList(),
                    r.antecedentes().stream().map(AntecedenteDto::de).toList());
        }
    }

    @PostMapping
    ResponseEntity<Map<String, String>> abrir(@RequestHeader("X-Profesional") String profesional,
                                              @RequestHeader(value = "X-Ambito", defaultValue = "CONSULTORIO") Ambito ambito,
                                              @RequestBody AperturaRequest r) {
        String documento = abrir.abrir(new AbrirHistoriaUseCase.Comando(profesional, ambito, r.documento(),
                r.nombre(), r.fechaNacimiento()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("documento", documento));
    }

    @GetMapping("/{documento}")
    HistoriaResponse consultar(@RequestHeader("X-Profesional") String profesional,
                               @RequestHeader(value = "X-Ambito", defaultValue = "CONSULTORIO") Ambito ambito,
                               @PathVariable String documento,
                               @RequestParam(required = false) String motivo) {
        return HistoriaResponse.de(consultar.consultar(
                new ConsultarHistoriaUseCase.Solicitud(profesional, ambito, documento, motivo)));
    }

    @GetMapping("/{documento}/resumen-urgencias")
    ResumenResponse resumen(@RequestHeader("X-Profesional") String profesional,
                               @RequestHeader(value = "X-Ambito", defaultValue = "URGENCIAS") Ambito ambito,
                               @PathVariable String documento,
                               @RequestParam(required = false) String motivo) {
        return ResumenResponse.de(consultar.resumenDeUrgencias(
                new ConsultarHistoriaUseCase.Solicitud(profesional, ambito, documento, motivo)));
    }

    @PostMapping("/{documento}/alergias")
    ResponseEntity<Map<String, String>> alergia(@RequestHeader("X-Profesional") String profesional,
                                                @RequestHeader(value = "X-Ambito", defaultValue = "CONSULTORIO") Ambito ambito,
                                                @PathVariable String documento, @RequestBody AlergiaRequest r) {
        String id = actualizar.registrarAlergia(new ActualizarHistoriaUseCase.ComandoAlergia(profesional, ambito,
                documento, r.sustancia(), r.severidad(), r.reaccion()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("alergiaId", id));
    }

    @PostMapping("/{documento}/antecedentes")
    ResponseEntity<Map<String, String>> antecedente(@RequestHeader("X-Profesional") String profesional,
                                                    @RequestHeader(value = "X-Ambito", defaultValue = "CONSULTORIO") Ambito ambito,
                                                    @PathVariable String documento, @RequestBody AntecedenteRequest r) {
        String id = actualizar.registrarAntecedente(new ActualizarHistoriaUseCase.ComandoAntecedente(profesional,
                ambito, documento, r.descripcion()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("antecedenteId", id));
    }

    @PostMapping("/{documento}/notas")
    ResponseEntity<Map<String, String>> nota(@RequestHeader("X-Profesional") String profesional,
                                             @RequestHeader(value = "X-Ambito", defaultValue = "CONSULTORIO") Ambito ambito,
                                             @PathVariable String documento, @RequestBody NotaRequest r) {
        String id = actualizar.agregarNota(new ActualizarHistoriaUseCase.ComandoNota(profesional, ambito,
                documento, r.texto()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("notaId", id));
    }

    @PostMapping("/{documento}/prescripciones")
    ResponseEntity<Map<String, String>> prescripcion(@RequestHeader("X-Profesional") String profesional,
                                                     @RequestHeader(value = "X-Ambito", defaultValue = "CONSULTORIO") Ambito ambito,
                                                     @PathVariable String documento, @RequestBody PrescripcionRequest r) {
        String id = actualizar.prescribir(new ActualizarHistoriaUseCase.ComandoPrescripcion(profesional, ambito,
                documento, r.medicamento(), r.dosis(), r.via(), r.frecuencia()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("prescripcionId", id));
    }

    @PostMapping("/{documento}/prescripciones/{prescripcionId}/administraciones")
    ResponseEntity<Map<String, String>> administracion(@RequestHeader("X-Profesional") String profesional,
                                                       @RequestHeader(value = "X-Ambito", defaultValue = "CONSULTORIO") Ambito ambito,
                                                       @PathVariable String documento, @PathVariable String prescripcionId,
                                                       @RequestBody AdministracionRequest r) {
        String id = actualizar.registrarAdministracion(new ActualizarHistoriaUseCase.ComandoAdministracion(
                profesional, ambito, documento, prescripcionId, r.estado(), r.observacion()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("administracionId", id));
    }

    @PostMapping("/{documento}/prescripciones/{prescripcionId}/eventos-adversos")
    ResponseEntity<Map<String, String>> eventoAdverso(@RequestHeader("X-Profesional") String profesional,
                                                      @RequestHeader(value = "X-Ambito", defaultValue = "CONSULTORIO") Ambito ambito,
                                                      @PathVariable String documento, @PathVariable String prescripcionId,
                                                      @RequestBody EventoAdversoRequest r) {
        String id = actualizar.reportarEventoAdverso(new ActualizarHistoriaUseCase.ComandoEventoAdverso(
                profesional, ambito, documento, prescripcionId, r.descripcion(), r.severidad()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("eventoAdversoId", id));
    }

    @GetMapping("/{documento}/bitacora")
    List<RegistroAcceso> bitacora(@RequestHeader("X-Profesional") String auditor,
                                  @RequestHeader(value = "X-Ambito", defaultValue = "CONSULTORIO") Ambito ambito,
                                  @PathVariable String documento) {
        return bitacora.consultar(auditor, ambito, documento);
    }

    @ExceptionHandler(ReglaDeNegocioVioladaException.class)
    ResponseEntity<Map<String, String>> reglaViolada(ReglaDeNegocioVioladaException e) {
        return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    ResponseEntity<Map<String, String>> accesoDenegado(AccesoDenegadoException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(HistoriaNoEncontradaException.class)
    ResponseEntity<Map<String, String>> noEncontrada(HistoriaNoEncontradaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }
}
