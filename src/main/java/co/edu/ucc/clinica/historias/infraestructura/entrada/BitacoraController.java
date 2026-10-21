package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.ConsultarBitacoraUseCase;
import co.edu.ucc.clinica.historias.dominio.Ambito;

/** Bitácora de accesos (solo auditor). */
@RestController
@RequestMapping("/historias/{documento}/bitacora")
class BitacoraController {
    private final ConsultarBitacoraUseCase bitacora;

    BitacoraController(ConsultarBitacoraUseCase bitacora) {
        this.bitacora = bitacora;
    }

    @GetMapping
    List<RegistroAccesoDto> consultar(@RequestHeader(Cabeceras.PROFESIONAL) String auditor,
                                      @RequestHeader(value = Cabeceras.AMBITO, defaultValue = "CONSULTORIO") Ambito ambito,
                                      @PathVariable String documento) {
        return bitacora.consultar(new ConsultarBitacoraUseCase.Solicitud(auditor, ambito, documento)).stream()
                .map(RegistroAccesoDto::de).toList();
    }
}
