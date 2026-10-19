# Historia clínica electrónica — arquitectura hexagonal

Misma estructura que `carnaval-tickets-st`, aplicada a otro negocio:

> Centro médico con urgencias 24 horas. Médicos y enfermeras consultan la historia desde consultorios
> y desde urgencias. En urgencias, no ver antecedentes y alergias a tiempo es un riesgo para la vida.
> Los datos de salud están protegidos por habeas data. Un auditor puede pedir en cualquier momento
> quién consultó o modificó una historia y cuándo. Unos 200 pacientes al día.

## Del enunciado a las reglas del dominio

| Lo que dice el negocio | Dónde quedó |
|---|---|
| En urgencias hay que ver alergias y antecedentes **ya** | `PoliticaDeAcceso.autorizarConsulta`: en `URGENCIAS` nunca se bloquea ni se exige motivo. `HistoriaClinica.resumenDeUrgencias` muestra primero las alergias graves. Endpoint corto `/resumen-urgencias`. |
| Datos sensibles (habeas data) | En `CONSULTORIO` se exige motivo. El `AUDITOR` no ve contenido clínico. Un usuario que no está en el directorio no entra. Cada acceso de urgencias avisa al oficial de protección de datos (`Notificador`). |
| El auditor pregunta quién consultó o modificó y cuándo | `RegistroAcceso` + puerto `BitacoraDeAccesos` (solo agregar y leer). También se registran los intentos **denegados**, los de usuarios **que no son del centro** (`rol: null`) y las propias revisiones del auditor. |
| Una historia clínica no se altera | Las notas, alergias y antecedentes solo se agregan; las correcciones son notas nuevas. El adaptador JPA solo hace inserciones. `Alergia`, `Antecedente` y `NotaClinica` no tienen constructor público: algo nuevo solo nace por los métodos de `HistoriaClinica`; los adaptadores usan `reconstruir`. |
| Ningún dato clínico se filtra por accidente | Se autoriza antes de buscar (sin permiso no se revela si el paciente existe). El REST responde con DTOs propios, no con las clases del dominio. |
| Roles distintos | La enfermera registra alergias y notas de enfermería; solo el médico registra antecedentes; el tipo de nota lo decide el rol. |
| Cambio y rastro van juntos | `HistoriasConfig.Transacciones`: el cambio y su registro en la bitácora se confirman en la misma transacción. Un acceso denegado no cambia nada, pero su registro sí se confirma. |
| MAR: registro de administración de medicamentos | Solo el médico prescribe (`HistoriaClinica.prescribir`), y nunca un medicamento al que el paciente es alérgico. Médicos y enfermeras registran cada dosis como `ADMINISTRADA`, `OMITIDA` o `RECHAZADA`; omitir o rechazar exige el motivo. Si la alergia aparece después de la orden, la dosis ya no se puede marcar como administrada. |
| Eventos adversos a medicamentos | `reportarEventoAdverso` solo acepta medicamentos que sí se administraron. Un evento `GRAVE` registra la alergia automáticamente, así quedan bloqueadas nuevas órdenes y dosis. La comparación es por nombre exacto: no reconoce familias (penicilina / amoxicilina). |
| 200 pacientes al día | Un monolito modular con Postgres sobra; no hace falta nada distribuido. |

## Equivalencias con el proyecto del carnaval

| carnaval-tickets-st | historia-clinica-st |
|---|---|
| `reservas/` | `historias/` |
| `Reserva`, `Localidad` | `HistoriaClinica`, `Alergia`, `Antecedente`, `NotaClinica`, `Profesional`, `Prescripcion`, `Administracion`, `EventoAdverso` |
| `EstadoReserva` | `Rol`, `Ambito`, `Severidad`, `TipoNota`, `TipoAcceso`, `ResultadoAcceso`, `EstadoAdministracion` |
| `ReservarBoletasUseCase`, `PagarReservaUseCase` | `AbrirHistoriaUseCase`, `ConsultarHistoriaUseCase`, `ActualizarHistoriaUseCase`, `ConsultarBitacoraUseCase` |
| `ReservaRepository`, `InventarioRepository` | `HistoriaClinicaRepository`, `BitacoraDeAccesos` |
| `PasarelaDePagos` (+ `PasarelaDePagosFalsa`) | `DirectorioDeProfesionales` (+ `DirectorioDeProfesionalesFalso`) |
| `Notificador` (correo al comprador) | `Notificador` (alerta al oficial de datos) |
| `Reloj` | `Reloj` |
| perfiles `memoria` / `postgres` | perfiles `memoria` / `postgres` |

```
src/main/java/co/edu/ucc/clinica/historias/
├── dominio/                      reglas puras, sin Spring
├── aplicacion/
│   ├── puertos/entrada/          casos de uso
│   ├── puertos/salida/           lo que el dominio necesita del mundo
│   └── servicios/                orquestan: identificar, decidir (dominio), guardar, dejar rastro
│                                 (Trazabilidad reúne lo común: directorio, bitácora, aviso)
└── infraestructura/
    ├── config/                   arma los servicios y las transacciones
    ├── entrada/                  REST
    └── salida/                   memoria/, jpa/, notificador y directorio
```

Las fronteras entre módulos las verifica Spring Modulith en `ModulosTest` (`modulos.verify()`), que además genera la documentación de módulos en `target/spring-modulith-docs`.

## Requisitos

- Java 21
- Spring Boot 4.1.1 + Spring Modulith (vienen en el `pom.xml`; se usa el wrapper `./mvnw`)
- Docker, solo para el perfil `postgres` (`docker-compose.yml` levanta la base `clinica`)

## Ejecutar

```bash
./mvnw test                                    # 45 pruebas (dominio, servicios, módulos y arranque)
./mvnw spring-boot:run                         # perfil memoria (por defecto)
docker compose up -d postgres
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

Usuarios de ejemplo (cabecera `X-Profesional`): `med-01`, `med-02` (médicos), `enf-01` (enfermera), `aud-01` (auditora).
Ámbito (cabecera `X-Ambito`): `CONSULTORIO` o `URGENCIAS`.
Pacientes cargados: `1085123456` y `27089123`.

> En producción el profesional saldría del token de sesión y el ámbito de la red o terminal, no de cabeceras libres.

```bash
# Urgencias: sin motivo, alergias graves primero
curl localhost:8080/historias/1085123456/resumen-urgencias -H "X-Profesional: med-01"

# Consultorio: sin motivo -> 403; con motivo -> 200
curl "localhost:8080/historias/1085123456?motivo=Control" -H "X-Profesional: med-02"

# Registrar alergia (enfermera)
curl -X POST localhost:8080/historias/1085123456/alergias -H "X-Profesional: enf-01" \
     -H "Content-Type: application/json" -d '{"sustancia":"Latex","severidad":"MODERADA","reaccion":"Rash"}'

# MAR: prescribir (médico), registrar la dosis (enfermera), reportar un evento adverso
curl -X POST localhost:8080/historias/27089123/prescripciones -H "X-Profesional: med-02" \
     -H "Content-Type: application/json" -d '{"medicamento":"Dipirona","dosis":"1 g","via":"IV","frecuencia":"Cada 8 horas"}'
curl -X POST localhost:8080/historias/27089123/prescripciones/{prescripcionId}/administraciones -H "X-Profesional: enf-01" \
     -H "Content-Type: application/json" -d '{"estado":"ADMINISTRADA"}'
curl -X POST localhost:8080/historias/27089123/prescripciones/{prescripcionId}/eventos-adversos -H "X-Profesional: enf-01" \
     -H "Content-Type: application/json" -d '{"descripcion":"Hipotensión","severidad":"GRAVE"}'

# Prescribir penicilina a 1085123456 (alérgica) -> 422
curl -X POST localhost:8080/historias/1085123456/prescripciones -H "X-Profesional: med-01" \
     -H "Content-Type: application/json" -d '{"medicamento":"Penicilina","dosis":"1 MUI","via":"IM","frecuencia":"Única"}'

# Bitácora (solo auditor)
curl localhost:8080/historias/1085123456/bitacora -H "X-Profesional: aud-01"
```

Códigos HTTP: `403` acceso denegado, `404` historia inexistente, `422` regla de negocio violada.

### Endpoints

Todos van bajo `/historias` y exigen la cabecera `X-Profesional`. `X-Ambito` vale por defecto `CONSULTORIO`, salvo en el resumen de urgencias, donde vale `URGENCIAS`.

| Método | Ruta | Cuerpo | Quién |
|---|---|---|---|
| `POST` | `/historias` | `{documento, nombre, fechaNacimiento}` | médico o enfermera |
| `GET` | `/{documento}?motivo=...` | — | médico o enfermera (motivo obligatorio en consultorio) |
| `GET` | `/{documento}/resumen-urgencias` | — | médico o enfermera |
| `POST` | `/{documento}/alergias` | `{sustancia, severidad, reaccion}` | médico o enfermera |
| `POST` | `/{documento}/antecedentes` | `{descripcion}` | solo médico |
| `POST` | `/{documento}/notas` | `{texto}` | médico o enfermera (el tipo de nota lo decide el rol) |
| `POST` | `/{documento}/prescripciones` | `{medicamento, dosis, via, frecuencia}` | solo médico |
| `POST` | `/{documento}/prescripciones/{id}/administraciones` | `{estado, observacion}` | médico o enfermera |
| `POST` | `/{documento}/prescripciones/{id}/eventos-adversos` | `{descripcion, severidad}` | médico o enfermera |
| `GET` | `/{documento}/bitacora` | — | solo auditor |

La consulta completa devuelve también el MAR: cada prescripción con sus dosis y sus eventos adversos.

## Ideas para seguir

- En Postgres, quitar `UPDATE` y `DELETE` sobre `registro_acceso` al usuario de la aplicación.
- Reemplazar `DirectorioDeProfesionalesFalso` por un adaptador real (perfil `identidad-remota`).
- Un adaptador de `Notificador` por correo (perfil `correo`).
