package co.edu.ucc.clinica.historias.infraestructura.entrada;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** El mismo contrato HTTP se ejecuta con memoria o con -Dspring.profiles.active=mariadb. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HistoriasHttpTest {
    @LocalServerPort
    int puerto;

    private final HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final JsonMapper json = JsonMapper.builder().build();

    @Test
    void lasConsultasConservanLosDtosYLasAlergiasGravesPrimero() throws Exception {
        JsonNode resumen = solicitar("GET", "/historias/1085123456/resumen-urgencias", "med-01", null, 200);
        assertEquals("1085123456", resumen.path("documento").asString());
        assertEquals("GRAVE", resumen.path("alergias").get(0).path("severidad").asString());
        assertEquals(2, resumen.path("antecedentes").size());

        JsonNode historia = solicitar("GET", "/historias/1085123456?motivo=Control", "med-01", null, 200);
        assertTrue(historia.has("fechaNacimiento"));
        assertTrue(historia.has("notas"));
        assertFalse(historia.has("documentoPaciente"));
        assertFalse(historia.has("medicacion"));
    }

    @Test
    void losErroresConservanSusCodigosYLosAccesosDenegadosQuedanRegistrados() throws Exception {
        solicitar("GET", "/historias/1085123456", "med-01", null, 403);
        solicitar("GET", "/historias/1085123456?motivo=Control", "aud-01", null, 403);
        solicitar("GET", "/historias/1085123456?motivo=Control", "desconocido", null, 403);
        solicitar("GET", "/historias/inexistente-" + UUID.randomUUID() + "?motivo=Control", "med-01", null, 404);
        solicitar("POST", "/historias/1085123456/alergias", "enf-01",
                """
                {"sustancia":"Penicilina","severidad":"GRAVE","reaccion":"Prueba duplicada"}
                """, 422);

        JsonNode bitacora = solicitar("GET", "/historias/1085123456/bitacora", "aud-01", null, 200);
        assertTrue(bitacora.findValuesAsString("resultado").contains("DENEGADO"));
        assertTrue(bitacora.findValuesAsString("profesionalId").contains("desconocido"));
    }

    @Test
    void laAperturaYLosRegistrosClinicosSeRecuperanConSuBitacora() throws Exception {
        String documento = "prueba-" + UUID.randomUUID();
        JsonNode apertura = solicitar("POST", "/historias", "med-01",
                """
                {"documento":"%s","nombre":"Paciente de prueba","fechaNacimiento":"1980-05-12"}
                """.formatted(documento), 201);
        assertEquals(documento, apertura.path("documento").asString());
        String ruta = "/historias/" + documento;

        solicitar("POST", ruta + "/alergias", "enf-01",
                """
                {"sustancia":"Latex de prueba","severidad":"MODERADA","reaccion":"Registro de prueba"}
                """, 201);
        solicitar("POST", ruta + "/antecedentes", "med-01", "{\"descripcion\":\"Antecedente de prueba\"}", 201);
        solicitar("POST", ruta + "/notas", "enf-01", "{\"texto\":\"Nota de prueba\"}", 201);

        JsonNode historia = solicitar("GET", ruta + "?motivo=Control", "med-01", null, 200);
        assertEquals(1, historia.path("alergias").size());
        assertEquals("Antecedente de prueba", historia.path("antecedentes").get(0).path("descripcion").asString());
        assertEquals("NOTA_ENFERMERIA", historia.path("notas").get(0).path("tipo").asString());
        JsonNode bitacora = solicitar("GET", ruta + "/bitacora", "aud-01", null, 200);
        assertTrue(bitacora.size() >= 4);
        assertTrue(bitacora.findValuesAsString("tipo").contains("APERTURA"));
        assertTrue(bitacora.findValuesAsString("tipo").contains("MODIFICACION"));
    }

    @Test
    void laApiYaNoPublicaLasOperacionesDeMedicacionRetiradas() throws Exception {
        String ruta = "/historias/1085123456/prescripciones";
        solicitar("POST", ruta, "med-01", "{}", 404);
        solicitar("POST", ruta + "/cualquiera/administraciones", "enf-01", "{}", 404);
        solicitar("POST", ruta + "/cualquiera/eventos-adversos", "enf-01", "{}", 404);
    }

    private JsonNode solicitar(String metodo, String ruta, String profesional, String cuerpo, int estado)
            throws Exception {
        var peticion = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + ruta))
                .timeout(Duration.ofSeconds(10)).header("X-Profesional", profesional);
        if (cuerpo == null) {
            peticion.GET();
        } else {
            peticion.header("Content-Type", "application/json")
                    .method(metodo, HttpRequest.BodyPublishers.ofString(cuerpo));
        }
        var respuesta = cliente.send(peticion.build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(estado, respuesta.statusCode(), () -> metodo + " " + ruta + ": " + respuesta.body());
        return json.readTree(respuesta.body());
    }
}
