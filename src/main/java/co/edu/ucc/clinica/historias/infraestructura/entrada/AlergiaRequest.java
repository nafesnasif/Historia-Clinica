package co.edu.ucc.clinica.historias.infraestructura.entrada;

import co.edu.ucc.clinica.historias.dominio.Severidad;

public record AlergiaRequest(String sustancia, Severidad severidad, String reaccion) {}
