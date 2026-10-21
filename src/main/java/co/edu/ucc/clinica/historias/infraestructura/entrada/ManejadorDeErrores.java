package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import co.edu.ucc.clinica.historias.dominio.AccesoDenegadoException;
import co.edu.ucc.clinica.historias.dominio.HistoriaNoEncontradaException;
import co.edu.ucc.clinica.historias.dominio.ReglaDeNegocioVioladaException;

/** Traduce las excepciones del dominio a códigos HTTP para todos los controladores. */
@RestControllerAdvice
class ManejadorDeErrores {

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
