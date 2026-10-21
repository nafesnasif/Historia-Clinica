package co.edu.ucc.clinica;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

/** Comprueba las fronteras internas que no cubre la verificación de módulos de Modulith. */
class ArquitecturaHexagonalTest {
    private static final String RAIZ = "co.edu.ucc.clinica.historias";
    private static final String DOMINIO = RAIZ + ".dominio";
    private static final String ENTRADA = RAIZ + ".aplicacion.puertos.entrada";
    private static final String SALIDA = RAIZ + ".aplicacion.puertos.salida";
    private static final String SERVICIOS = RAIZ + ".aplicacion.servicios";
    private static final String HTTP = RAIZ + ".infraestructura.entrada";
    private static final String ADAPTADORES = RAIZ + ".infraestructura.salida";

    private static final JavaClasses CLASES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(RAIZ);

    @Test
    void conservaLosMismosPaquetesDelEjemploCarnaval() {
        classes().should().resideInAnyPackage(DOMINIO, ENTRADA, SALIDA, SERVICIOS, HTTP,
                ADAPTADORES, ADAPTADORES + ".memoria", ADAPTADORES + ".jpa", RAIZ + ".infraestructura.config")
                .check(CLASES);
    }

    @Test
    void elDominioSoloDependeDeJavaYDelPropioDominio() {
        classes().that().resideInAPackage(DOMINIO).should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", DOMINIO).check(CLASES);
    }

    @Test
    void losPuertosDeEntradaNoConocenServiciosNiAdaptadores() {
        classes().that().resideInAPackage(ENTRADA).should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", DOMINIO, ENTRADA).check(CLASES);
    }

    @Test
    void losPuertosDeSalidaNoConocenImplementaciones() {
        classes().that().resideInAPackage(SALIDA).should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", DOMINIO, SALIDA).check(CLASES);
    }

    @Test
    void losServiciosSoloConocenElNucleo() {
        classes().that().resideInAPackage(SERVICIOS).should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", DOMINIO, ENTRADA, SALIDA, SERVICIOS).check(CLASES);
    }

    @Test
    void laEntradaHttpUsaLosPuertosDeEntrada() {
        classes().that().resideInAPackage(HTTP).should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", "org.springframework..", DOMINIO, ENTRADA, HTTP).check(CLASES);
    }

    @Test
    void losAdaptadoresDeSalidaSoloConocenSuContratoYLaTecnologia() {
        classes().that().resideInAPackage(ADAPTADORES + "..").should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", "org.springframework..", "jakarta.persistence..",
                        DOMINIO, SALIDA, ADAPTADORES + "..").check(CLASES);
    }
}
