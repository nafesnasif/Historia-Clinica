package co.edu.ucc.clinica.historias.aplicacion.servicios;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import co.edu.ucc.clinica.historias.aplicacion.puertos.entrada.SolicitudDeConsulta;
import co.edu.ucc.clinica.historias.dominio.*;

class ConsultarHistoriaServiceTest {
    final Falsos f = new Falsos();
    final ConsultarHistoriaService servicio = new ConsultarHistoriaService(f.lectura);
    final ConsultarResumenDeUrgenciasService resumen = new ConsultarResumenDeUrgenciasService(f.lectura, f.reloj);

    @Test
    void cadaConsultaQuedaEnLaBitacora() {
        f.conPaciente("1085");
        servicio.consultar(new SolicitudDeConsulta("med-01", Ambito.CONSULTORIO, "1085", "Control"));
        var registro = f.bitacora.registros.getFirst();
        assertEquals("med-01", registro.profesionalId());
        assertEquals(TipoAcceso.CONSULTA, registro.tipo());
        assertEquals(ResultadoAcceso.PERMITIDO, registro.resultado());
        assertEquals(Falsos.AHORA, registro.ocurridoEn());
        assertTrue(f.notificador.alertas.isEmpty());
    }

    @Test
    void enUrgenciasVeLasAlergiasSinMotivoYSeAvisaAlOficialDeDatos() {
        f.conPaciente("1085");
        var r = resumen.consultar(new SolicitudDeConsulta("med-01", Ambito.URGENCIAS, "1085", null));
        assertEquals("Penicilina", r.alergias().getFirst().sustancia());
        assertEquals(TipoAcceso.CONSULTA_URGENCIAS, f.bitacora.registros.getFirst().tipo());
        assertEquals(1, f.notificador.alertas.size());
    }

    @Test
    void elAccesoDenegadoTambienQuedaEnLaBitacora() {
        f.conPaciente("1085");
        assertThrows(AccesoDenegadoException.class,
                () -> servicio.consultar(new SolicitudDeConsulta("aud-01", Ambito.CONSULTORIO, "1085", "Curiosidad")));
        var registro = f.bitacora.registros.getFirst();
        assertEquals("aud-01", registro.profesionalId());
        assertEquals(ResultadoAcceso.DENEGADO, registro.resultado());
    }

    @Test
    void sinPermisoNoSeRevelaSiElPacienteExiste() {
        // el documento 999 no existe: aun así la respuesta es "acceso denegado", no "no encontrada"
        assertThrows(AccesoDenegadoException.class,
                () -> servicio.consultar(new SolicitudDeConsulta("aud-01", Ambito.CONSULTORIO, "999", "Revisión")));
        assertEquals(ResultadoAcceso.DENEGADO, f.bitacora.registros.getFirst().resultado());
    }

    @Test
    void unUsuarioDesconocidoNoEntraPeroSuIntentoQuedaEnLaBitacora() {
        f.conPaciente("1085");
        assertThrows(AccesoDenegadoException.class,
                () -> servicio.consultar(new SolicitudDeConsulta("intruso", Ambito.URGENCIAS, "1085", null)));
        var registro = f.bitacora.registros.getFirst();
        assertEquals("intruso", registro.profesionalId());
        assertTrue(registro.esDeDesconocido());
        assertEquals(ResultadoAcceso.DENEGADO, registro.resultado());
        assertEquals(TipoAcceso.CONSULTA_URGENCIAS, registro.tipo());
        assertTrue(f.notificador.alertas.isEmpty()); // no se trata como acceso de urgencias permitido
    }

    @Test
    void sinIdentificadorTambienQuedaRegistrado() {
        assertThrows(AccesoDenegadoException.class,
                () -> servicio.consultar(new SolicitudDeConsulta(null, Ambito.CONSULTORIO, "1085", "x")));
        assertEquals("(vacío)", f.bitacora.registros.getFirst().profesionalId());
    }
}
