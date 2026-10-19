package co.edu.ucc.clinica;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulosTest {

    ApplicationModules modulos = ApplicationModules.of(ClinicaApplication.class);

    @Test
    void lasFronterasSeRespetan() {
        modulos.forEach(System.out::println); // imprime cada módulo y sus dependencias
        modulos.verify();
    }

    @Test
    void generarDocumentacion() {
        new Documenter(modulos).writeDocumentation();
    }
}
