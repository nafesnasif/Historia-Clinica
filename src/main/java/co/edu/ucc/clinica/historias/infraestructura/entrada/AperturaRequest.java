package co.edu.ucc.clinica.historias.infraestructura.entrada;

import java.time.LocalDate;

public record AperturaRequest(String documento, String nombre, LocalDate fechaNacimiento) {}
