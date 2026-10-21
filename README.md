# Hospital: historias clínicas con arquitectura hexagonal

El proyecto conserva la estructura de paquetes de `carnaval-tickets-st`, aplicada a historias clínicas.
Tiene dos alternativas de almacenamiento: **memoria** y **MariaDB**. Al arrancar, la configuración comprueba la
conexión: si MariaDB está disponible la utiliza; si no, arranca automáticamente en memoria. Docker puede estar
cerrado o no estar instalado. Los casos de uso y el dominio son los mismos en ambos perfiles.

## Estructura

```text
src/main/java/co/edu/ucc/clinica/
├── ClinicaApplication.java
└── historias/
    ├── dominio/
    ├── aplicacion/
    │   ├── puertos/
    │   │   ├── entrada/
    │   │   └── salida/
    │   └── servicios/
    └── infraestructura/
        ├── config/
        ├── entrada/
        └── salida/
            ├── memoria/
            └── jpa/
```

| Lugar | Responsabilidad |
|---|---|
| `dominio` | Historia clínica, alergias, antecedentes, notas, profesionales, permisos y reglas de acceso. Solo depende de Java y del propio dominio. |
| `aplicacion/puertos/entrada` | Siete casos de uso, sus comandos y solicitudes. |
| `aplicacion/puertos/salida` | Contratos para consultar y guardar historias, registrar y consultar accesos, identificar profesionales, notificar y obtener la hora. |
| `aplicacion/servicios` | Un servicio por caso de uso y auxiliares para identificación, lecturas, modificaciones y trazabilidad. No conocen Spring, HTTP, JPA ni implementaciones de almacenamiento. |
| `infraestructura/entrada` | Controladores HTTP, solicitudes, respuestas DTO y traducción de errores del dominio. Dependen de los puertos de entrada. |
| `infraestructura/salida/memoria` | Historias y bitácora en mapas y listas del proceso. |
| `infraestructura/salida/jpa` | Adaptadores para MariaDB, entidades JPA, repositorios Spring Data y `MapeadorJpa`. Las entidades de persistencia están separadas del dominio. |
| `infraestructura/salida` | Directorio de profesionales de ejemplo y notificador por consola. |
| `infraestructura/config` | Ensambla los servicios, el reloj y las transacciones. |

El recorrido es: HTTP → controlador → puerto de entrada → servicio → dominio y puertos de salida → adaptadores.
En el código, los adaptadores implementan los puertos; la aplicación no importa los adaptadores.

`ComponentesComunesConfig` construye los auxiliares. `CasosDeUsoConfig` conecta cada servicio a su puerto de entrada.
`SeleccionDeAlmacenamiento` elige el perfil antes de configurar JPA. Solo comprueba una conexión JDBC, sin invocar
Docker. El chequeo tiene tiempos de espera acotados. El perfil `memoria` se puede forzar para omitir ese chequeo.
En memoria se utiliza `SinTransaccion`; con MariaDB, `TransaccionConBitacora` confirma el cambio y su registro juntos.
Cuando se deniega un acceso, se conserva el registro del intento y después se devuelve el error.

## Funciones

- Abrir y consultar una historia clínica.
- Consultar el resumen de urgencias con las alergias graves primero.
- Agregar alergias, antecedentes y notas, sin editar los registros anteriores.
- Registrar quién accedió, cuándo, desde qué ámbito y si el acceso se permitió o denegó.
- Consultar la bitácora con el rol de auditor.

La medicación, las prescripciones, las administraciones de dosis y los eventos adversos se retiraron por la
corrección del alcance: lo solicitado era **MariaDB**, la base de datos.

| Regla | Implementación |
|---|---|
| En consultorio se exige motivo para consultar; en urgencias no. | `PoliticaDeAcceso`, `Ambito`. |
| Médicos y enfermeras pueden consultar, abrir historias, registrar alergias y escribir notas. | `Rol`, `Permiso`, `HistoriaClinica`. |
| Solo el médico registra antecedentes. | `Permiso.REGISTRAR_ANTECEDENTE`. |
| El tipo de nota depende del rol. | `Rol.tipoDeNota`. |
| El auditor consulta accesos y no el contenido clínico. | `PoliticaDeAcceso.autorizarAuditoria`. |
| Los intentos denegados y los profesionales desconocidos dejan rastro. | `IdentificacionDeProfesionales`, `RastroDeAccesos`. |
| Los accesos permitidos de urgencias avisan al oficial de datos. | `AvisoAlOficialDeDatos` mediante `Notificador`. |

El directorio de ejemplo y el notificador de consola son adaptadores de demostración.
El notificador solo imprime; las cabeceras identifican al profesional y al ámbito en esta aplicación de ejemplo.

## Ejecutar, incluso con Docker cerrado

Requiere Java 21. Maven se ejecuta mediante el wrapper incluido.

```powershell
.\mvnw.cmd spring-boot:run
```

Si MariaDB no responde, Hospital selecciona `memoria` y arranca sin crear una conexión de datos ni inicializar JPA.
Los pacientes de ejemplo se cargan desde `HistoriaEnMemoria`. Los cambios en memoria duran mientras vive el proceso;
no se sincronizan automáticamente con MariaDB. La selección se realiza al arrancar.

Para trabajar siempre en memoria sin comprobar el servidor:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=memoria"
```

## Ejecutar con MariaDB sin Docker

La aplicación se conecta por JDBC a una instalación normal de MariaDB, local o remota. No ejecuta Docker ni
necesita que Docker esté instalado. El servicio MariaDB debe estar iniciado, igual que cualquier servidor de base de datos.

En una instalación local, un administrador puede preparar la base y el usuario una sola vez:

```sql
CREATE DATABASE IF NOT EXISTS clinica CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'clinica'@'localhost' IDENTIFIED BY 'clinica';
GRANT ALL PRIVILEGES ON clinica.* TO 'clinica'@'localhost';
```

Después se inicia el backend:

```powershell
.\mvnw.cmd spring-boot:run
```

Con la base accesible se selecciona `mariadb`. Incluso si se solicita expresamente ese perfil y la conexión no
responde, la configuración permite arrancar con memoria.

Spring crea las tablas mediante JPA y luego ejecuta `data.sql`. El script puede ejecutarse nuevamente sin duplicar
los pacientes y registros iniciales ni reemplazar los existentes. Los instantes se guardan en UTC; el dominio
calcula las fechas del centro en `America/Bogota`.

La conexión se ajusta con estas variables:

| Variable | Predeterminado | Uso |
|---|---|---|
| `DB_HOST` | `localhost` | Servidor MariaDB. |
| `DB_PORT` | `3306` | Puerto de conexión y puerto publicado por Compose. |
| `DB_NAME` | `clinica` | Base de datos. |
| `DB_USER` | `clinica` | Usuario de la aplicación. |
| `DB_PASSWORD` | `clinica` | Contraseña local de la aplicación. |
| `DB_ROOT_PASSWORD` | `clinica-root` | Contraseña root del contenedor; la aplicación no la utiliza. |

Ejemplo en PowerShell:

```powershell
$env:DB_HOST = 'localhost'
$env:DB_PORT = '3306'
$env:DB_NAME = 'clinica'
$env:DB_USER = 'clinica'
$env:DB_PASSWORD = 'clinica'
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mariadb"
```

## Docker es una alternativa opcional

Si se prefiere disponer de MariaDB en un contenedor, se incluye `docker-compose.yml`. El contenedor proporciona
el mismo servidor al que se conecta el adaptador JPA; la aplicación no conoce cómo se inició ese servidor.

```powershell
docker compose up -d --wait mariadb
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mariadb"
```

Compose utiliza MariaDB 11.8 y un volumen para conservar los datos. No interviene al ejecutar con memoria ni
al conectarse a una instalación normal de MariaDB.

## SOLID dentro de la estructura de Carnaval

| Principio | Aplicación en Hospital |
|---|---|
| Responsabilidad única | Dominio para reglas; servicios para coordinar; controladores para HTTP; mapeador para conversión JPA; configuración para ensamblaje y transacciones. |
| Abierto a extensión | Se pueden agregar adaptadores de los puertos y oyentes de accesos sin cambiar el dominio ni los servicios que los utilizan. |
| Sustitución | Memoria y JPA implementan los mismos puertos. El mismo contrato HTTP se comprueba con ambos perfiles. |
| Segregación de interfaces | Un puerto por caso de uso; lectura y escritura de historias y de bitácora tienen contratos separados. |
| Inversión de dependencias | Los servicios reciben puertos por constructor. Spring y los adaptadores quedan en infraestructura. |

## API

Todas las rutas están bajo `/historias` y requieren `X-Profesional`.
`X-Ambito` vale `CONSULTORIO` por defecto, salvo en el resumen de urgencias, donde vale `URGENCIAS`.

Profesionales de ejemplo: `med-01`, `med-02`, `enf-01`, `aud-01`.
Pacientes iniciales: `1085123456` y `27089123`.

| Método | Ruta | Cuerpo | Quién |
|---|---|---|---|
| `POST` | `/historias` | `{documento, nombre, fechaNacimiento}` | Médico o enfermera. |
| `GET` | `/historias/{documento}?motivo=...` | — | Médico o enfermera. |
| `GET` | `/historias/{documento}/resumen-urgencias` | — | Médico o enfermera. |
| `POST` | `/historias/{documento}/alergias` | `{sustancia, severidad, reaccion}` | Médico o enfermera. |
| `POST` | `/historias/{documento}/antecedentes` | `{descripcion}` | Médico. |
| `POST` | `/historias/{documento}/notas` | `{texto}` | Médico o enfermera. |
| `GET` | `/historias/{documento}/bitacora` | — | Auditor. |

Las respuestas usan DTO propios de la entrada HTTP. Códigos de error: `403` acceso denegado,
`404` historia inexistente y `422` regla de negocio violada.

```powershell
curl.exe "http://localhost:8080/historias/1085123456/resumen-urgencias" -H "X-Profesional: med-01"
curl.exe "http://localhost:8080/historias/1085123456?motivo=Control" -H "X-Profesional: med-01"
curl.exe "http://localhost:8080/historias/1085123456/bitacora" -H "X-Profesional: aud-01"
```

## Verificación

```powershell
.\mvnw.cmd -B clean test
```

Incluye pruebas del dominio, servicios sin Spring, módulos, arranque, siete reglas de arquitectura con ArchUnit
y pruebas HTTP. `ArranqueSinBaseDeDatosTest` solicita MariaDB en un puerto sin servidor y comprueba que los casos
de uso funcionan con memoria y sin un `DataSource`. `ArquitecturaHexagonalTest` comprueba la estructura de paquetes y evita dependencias del núcleo
hacia Spring o infraestructura, y dependencias directas de los controladores hacia servicios o persistencia.
Spring Modulith sigue comprobando los módulos y genera documentación en `target/spring-modulith-docs`.

Con MariaDB disponible, el mismo contrato HTTP puede verificarse contra la base real:

```powershell
.\mvnw.cmd -B test "-Dtest=HistoriasHttpTest" "-Dspring.profiles.active=mariadb"
```

Las pruebas HTTP agregan un paciente con un documento único de prueba; para ejecutarlas conviene seleccionar
una base de pruebas mediante `DB_NAME`.

## Referencia del curso

La separación de responsabilidades y las dependencias siguen las diapositivas del Módulo 2 aportadas por el usuario:
SOLID (2 y 25), independencia de Docker (24 y 50), núcleo sin frameworks y ensamble (58), estructura de paquetes (62)
y errores que se deben evitar (67). Los ejemplos de descuentos, ingreso, pagos y colas ilustran conceptos del curso;
no agregan requisitos funcionales a Hospital.
