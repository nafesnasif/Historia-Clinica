package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.AgregarNotaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.RegistrarAlergiaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.RegistrarAntecedenteUseCase;
import co.edu.ucc.clinica.historias.dominio.Ambito;

/** Alergias, antecedentes y notas: lo que se agrega a la historia fuera de la medicación. */
@RestController
@RequestMapping("/historias/{documento}")
class RegistroClinicoController {
    private final RegistrarAlergiaUseCase alergias;
    private final RegistrarAntecedenteUseCase antecedentes;
    private final AgregarNotaUseCase notas;

    RegistroClinicoController(RegistrarAlergiaUseCase alergias, RegistrarAntecedenteUseCase antecedentes,
                              AgregarNotaUseCase notas) {
        this.alergias = alergias;
        this.antecedentes = antecedentes;
        this.notas = notas;
    }

    @PostMapping("/alergias")
    ResponseEntity<Map<String, String>> alergia(@RequestHeader(Cabeceras.PROFESIONAL) String profesional,
                                                @RequestHeader(value = Cabeceras.AMBITO, defaultValue = "CONSULTORIO") Ambito ambito,
                                                @PathVariable String documento, @RequestBody AlergiaRequest r) {
        String id = alergias.registrar(new RegistrarAlergiaUseCase.Comando(profesional, ambito, documento,
                r.sustancia(), r.severidad(), r.reaccion()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("alergiaId", id));
    }

    @PostMapping("/antecedentes")
    ResponseEntity<Map<String, String>> antecedente(@RequestHeader(Cabeceras.PROFESIONAL) String profesional,
                                                    @RequestHeader(value = Cabeceras.AMBITO, defaultValue = "CONSULTORIO") Ambito ambito,
                                                    @PathVariable String documento, @RequestBody AntecedenteRequest r) {
        String id = antecedentes.registrar(new RegistrarAntecedenteUseCase.Comando(profesional, ambito, documento,
                r.descripcion()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("antecedenteId", id));
    }

    @PostMapping("/notas")
    ResponseEntity<Map<String, String>> nota(@RequestHeader(Cabeceras.PROFESIONAL) String profesional,
                                             @RequestHeader(value = Cabeceras.AMBITO, defaultValue = "CONSULTORIO") Ambito ambito,
                                             @PathVariable String documento, @RequestBody NotaRequest r) {
        String id = notas.agregar(new AgregarNotaUseCase.Comando(profesional, ambito, documento, r.texto()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("notaId", id));
    }
}
