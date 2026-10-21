package co.edu.ucc.clinica.historias.infraestructura.config;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/** Selecciona el almacenamiento antes de que Spring configure la conexión y JPA. No utiliza Docker. */
public class SeleccionDeAlmacenamiento implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment entorno, SpringApplication aplicacion) {
        var perfiles = new ArrayList<>(Arrays.asList(entorno.getActiveProfiles()));
        boolean memoriaSolicitada = perfiles.contains("memoria");
        boolean disponible = !memoriaSolicitada && mariaDbDisponible(entorno);
        perfiles.removeIf(p -> p.equals("mariadb") || p.equals("memoria"));
        perfiles.add(disponible ? "mariadb" : "memoria");
        entorno.setActiveProfiles(perfiles.toArray(String[]::new));
        entorno.getPropertySources().addFirst(new MapPropertySource("hospitalAlmacenamiento",
                Map.of("spring.profiles.active", String.join(",", perfiles))));
        System.getLogger(getClass().getName()).log(System.Logger.Level.INFO,
                disponible ? "MariaDB disponible: se utilizará persistencia JPA."
                        : "Se utilizará memoria; la aplicación funciona sin MariaDB ni Docker.");
    }

    private boolean mariaDbDisponible(ConfigurableEnvironment entorno) {
        String host = entorno.getProperty("DB_HOST", "localhost");
        String puerto = entorno.getProperty("DB_PORT", "3306");
        String base = entorno.getProperty("DB_NAME", "clinica");
        var propiedades = new Properties();
        propiedades.setProperty("user", entorno.getProperty("DB_USER", "clinica"));
        propiedades.setProperty("password", entorno.getProperty("DB_PASSWORD", "clinica"));
        propiedades.setProperty("connectTimeout", "1500");
        propiedades.setProperty("socketTimeout", "1500");
        try (var conexion = DriverManager.getConnection("jdbc:mariadb://" + host + ":" + puerto + "/" + base,
                propiedades)) {
            return conexion.isValid(1);
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public int getOrder() {
        return ConfigDataEnvironmentPostProcessor.ORDER - 1;
    }
}
