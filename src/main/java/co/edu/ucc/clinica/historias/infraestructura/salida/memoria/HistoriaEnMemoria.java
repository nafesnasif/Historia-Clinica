package co.edu.ucc.clinica.historias.infraestructura.salida.memoria;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import co.edu.ucc.clinica.historias.aplicacion.puertos.salida.HistoriaClinicaRepository;
import co.edu.ucc.clinica.historias.dominio.*;

@Component
@Profile("memoria")
public class HistoriaEnMemoria implements HistoriaClinicaRepository {

    private final Map<String, HistoriaClinica> datos = new ConcurrentHashMap<>();

    HistoriaEnMemoria() {
        Instant apertura = Instant.parse("2026-01-10T08:00:00Z");
        guardar(HistoriaClinica.reconstruir("1085123456", "María Fernanda Guerrero", LocalDate.of(1980, 5, 12),
                "med-01", apertura,
                List.of(Alergia.reconstruir("a-1", "Ibuprofeno", Severidad.LEVE, "Urticaria", "enf-01", apertura),
                        Alergia.reconstruir("a-2", "Penicilina", Severidad.GRAVE, "Anafilaxia", "med-01", apertura)),
                List.of(Antecedente.reconstruir("an-1", "Hipertensión arterial en tratamiento con losartán", "med-01", apertura),
                        Antecedente.reconstruir("an-2", "Diabetes mellitus tipo 2", "med-01", apertura)),
                List.of()));
        guardar(HistoriaClinica.reconstruir("27089123", "José Luis Pantoja", LocalDate.of(1955, 11, 3),
                "med-02", apertura,
                List.of(Alergia.reconstruir("a-3", "Látex", Severidad.MODERADA, "Dermatitis de contacto", "enf-01", apertura)),
                List.of(Antecedente.reconstruir("an-3", "Infarto agudo de miocardio en 2019", "med-02", apertura),
                        Antecedente.reconstruir("an-4", "Anticoagulado con warfarina", "med-02", apertura)),
                List.of()));
    }

    @Override
    public void guardar(HistoriaClinica h) {
        datos.put(h.documentoPaciente(), h);
    }

    @Override
    public Optional<HistoriaClinica> buscarPorDocumento(String documento) {
        return Optional.ofNullable(datos.get(documento));
    }

    @Override
    public boolean existe(String documento) {
        return datos.containsKey(documento);
    }
}
