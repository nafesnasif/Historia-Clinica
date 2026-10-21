package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.AbrirHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarHistoriaUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarResumenDeUrgenciasUseCase;
import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.SolicitudDeConsulta;
import co.edu.ucc.clinica.historias.dominio.Ambito;

/** Abrir y consultar una historia. */
@RestController
@RequestMapping("/historias")
class HistoriaController {
    private final AbrirHistoriaUseCase abrir;
    private final ConsultarHistoriaUseCase consultar;
    private final ConsultarResumenDeUrgenciasUseCase resumen;

    HistoriaController(AbrirHistoriaUseCase abrir, ConsultarHistoriaUseCase consultar,
                       ConsultarResumenDeUrgenciasUseCase resumen) {
        this.abrir = abrir;
        this.consultar = consultar;
        this.resumen = resumen;
    }

    @PostMapping
    ResponseEntity<Map<String, String>> abrir(@RequestHeader(Cabeceras.PROFESIONAL) String profesional,
                                              @RequestHeader(value = Cabeceras.AMBITO, defaultValue = "CONSULTORIO") Ambito ambito,
                                              @RequestBody AperturaRequest r) {
        String documento = abrir.abrir(new AbrirHistoriaUseCase.Comando(profesional, ambito, r.documento(),
                r.nombre(), r.fechaNacimiento()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("documento", documento));
    }

    @GetMapping("/{documento}")
    HistoriaResponse consultar(@RequestHeader(Cabeceras.PROFESIONAL) String profesional,
                               @RequestHeader(value = Cabeceras.AMBITO, defaultValue = "CONSULTORIO") Ambito ambito,
                               @PathVariable String documento,
                               @RequestParam(required = false) String motivo) {
        return HistoriaResponse.de(consultar.consultar(new SolicitudDeConsulta(profesional, ambito, documento, motivo)));
    }

    @GetMapping("/{documento}/resumen-urgencias")
    ResumenResponse resumen(@RequestHeader(Cabeceras.PROFESIONAL) String profesional,
                            @RequestHeader(value = Cabeceras.AMBITO, defaultValue = "URGENCIAS") Ambito ambito,
                            @PathVariable String documento,
                            @RequestParam(required = false) String motivo) {
        return ResumenResponse.de(resumen.consultar(new SolicitudDeConsulta(profesional, ambito, documento, motivo)));
    }
}
