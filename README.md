# Cobros API — demo-cobros

Servicio API REST para **gestionar cobros automáticos simulados**: alta de clientes, registro
de cobros, procesamiento individual y por lotes (idempotente) y trazabilidad de eventos en
auditoría.

- **Stack:** Java 21 · Quarkus 3.40.1.2 (JVM) · Gradle · SQLite
- **Arquitectura:** *Screaming Architecture* + *Onion estricta* 
- **Requisitos originales:** [`REQUIREMENTS.md`](./REQUIREMENTS.md)

---

## 1. Requisitos previos

- **JDK 21**
- **Docker** o **Podman** (solo si quieres ejecutar en contenedor)

No hace falta instalar Gradle ni Maven: el proyecto incluye el wrapper `./gradlew`.

---

## 2. Cómo ejecutar

### 2.1 Modo desarrollo (live coding)

```bash
./gradlew quarkusDev
```

- API: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/q/swagger-ui>
- OpenAPI: <http://localhost:8080/q/openapi>
- Health: <http://localhost:8080/q/health>

La base SQLite se crea en `./data/cobros.db` (ignorada por Git).

### 2.2 Pruebas

```bash
./gradlew test
```

### 2.3 Build y ejecución del JAR (JVM)

```bash
./gradlew build
java -jar build/quarkus-app/quarkus-run.jar
```

### 2.4 Contenedor (Docker o Podman)

El `Dockerfile.jvm` es **multi-stage**: una etapa *builder* compila el binario desde el código
fuente (con el Gradle wrapper) y otra etapa *runtime* usa una imagen JRE mínima. **No hace
falta ejecutar `./gradlew build` antes**: el contenedor compila el binario.

```bash
docker build -f src/main/docker/Dockerfile.jvm -t cobros-demo-jvm .
docker run -i --rm -p 8080:8080 -v cobros-data:/deployments/data cobros-demo-jvm
```

Con Podman es idéntico (cambia `docker` por `podman`):

```bash
podman build -f src/main/docker/Dockerfile.jvm -t cobros-demo-jvm .
podman run -i --rm -p 8080:8080 -v cobros-data:/deployments/data cobros-demo-jvm
```

> El volumen `cobros-data:/deployments/data` conserva la base SQLite **y el token** entre
> reinicios.
>
> Los tests no se ejecutan durante el build de la imagen (por velocidad); se corren con
> `./gradlew test`.

---

## 3. Seguridad — token de acceso

Todas las rutas requieren la cabecera **`X-API-Token`**, excepto los endpoints de
infraestructura bajo `/q/*` (health, OpenAPI, Swagger UI).

### 3.1 En desarrollo

`src/main/resources/application.properties` define `app.security.api-token=change-me`.
Puedes sobreescribirlo:

```bash
APP_SECURITY_API_TOKEN=mi-token ./gradlew quarkusDev
```

### 3.2 En el contenedor (token generado automáticamente)

Al **crear/arrancar el contenedor**, el entrypoint genera un token aleatorio la primera vez,
lo persiste en `/deployments/data/api-token` y lo imprime en los logs:

```
======================================================================
  Cobros API - token de acceso (cabecera X-API-Token):
  4f1a9c0b7e...  (64 caracteres hex)
======================================================================
```

**Cómo obtener el token:**

```bash
# desde los logs del contenedor
docker logs <contenedor> | grep -A1 "token de acceso"

# o leyendo el archivo dentro del contenedor
docker exec <contenedor> cat /deployments/data/api-token
```

**Fijar tu propio token** (en lugar de autogenerarlo):

```bash
docker run -i --rm -p 8080:8080 -e APP_SECURITY_API_TOKEN=mi-token cobros-demo-jvm
```

**Usar el token en las peticiones:**

```bash
TOKEN=$(docker exec <contenedor> cat /deployments/data/api-token)
curl -H "X-API-Token: $TOKEN" http://localhost:8080/clientes/1
```

Si el token falta o es incorrecto, la API responde **401** con el formato de error uniforme.

> **Alternativa productiva:** para un entorno real se recomienda JWT/OIDC
> (`quarkus-oidc`) en lugar de un token estático compartido.

---

## 4. Endpoints y ejemplos de uso

Base URL: `http://localhost:8080`

```bash
BASE=http://localhost:8080
TOKEN=change-me          # en dev; en el contenedor usa el token autogenerado (sección 3.2)
AUTH="X-API-Token: $TOKEN"
JSON="Content-Type: application/json"
```

### 4.1 Crear cliente — `POST /clientes`

Valida que el DPI sea único (13 dígitos).

```bash
curl -i -X POST "$BASE/clientes" -H "$JSON" -H "$AUTH" \
  -d '{"nombre":"Ana Pérez","dpi":"1234567890123","email":"ana@example.com","telefono":"5555-1234"}'
```

```http
HTTP/1.1 201 Created
Location: http://localhost:8080/clientes/1

{"id":1,"nombre":"Ana Pérez","dpi":"1234567890123","email":"ana@example.com","telefono":"5555-1234"}
```

### 4.2 Obtener cliente — `GET /clientes/{id}`

```bash
curl -s "$BASE/clientes/1" -H "$AUTH"
```

### 4.3 Registrar cobro — `POST /cobros`

Valida `monto > 0` y que el cliente exista. El cobro nace en estado **PENDIENTE**.

```bash
curl -i -X POST "$BASE/cobros" -H "$JSON" -H "$AUTH" \
  -d '{"clienteId":1,"monto":500,"moneda":"GTQ"}'
```

```json
{"id":1,"clienteId":1,"monto":500,"moneda":"GTQ","estado":"PENDIENTE",
 "fechaCreacion":"2026-11-18T00:41:27.644Z","fechaProceso":null,
 "referenciaExterna":"2492a37e-d5d9-4d14-8f65-d2a1259be51e"}
```

### 4.4 Procesar cobro — `POST /cobros/{id}/procesar`

Regla simulada: `monto ≤ 1000 → PROCESADO`, `monto > 1000 → FALLIDO`. Idempotente: si el cobro
ya está en estado terminal no se reprocesa.

```bash
curl -s -X POST "$BASE/cobros/1/procesar" -H "$AUTH"
```

```json
{"id":1,"clienteId":1,"monto":500,"moneda":"GTQ","estado":"PROCESADO",
 "fechaCreacion":"2026-11-18T00:41:27.644Z","fechaProceso":"2026-11-18T00:41:27.671Z",
 "referenciaExterna":"2492a37e-d5d9-4d14-8f65-d2a1259be51e"}
```

### 4.5 Procesar lote — `POST /cobros/lotes/procesar`

Recibe una lista de IDs y devuelve un resumen. **Idempotente**: los cobros ya terminales se
**omiten**; los IDs inexistentes se reportan como `noEncontrados`.

```bash
curl -s -X POST "$BASE/cobros/lotes/procesar" -H "$JSON" -H "$AUTH" \
  -d '{"cobroIds":[1,2,3,999999]}'
```

```json
{
  "total": 4,
  "procesados": 1,
  "fallidos": 1,
  "omitidos": 1,
  "noEncontrados": 1,
  "detalle": [
    {"id":1,"estado":"PROCESADO","resultado":"PROCESADO","mensaje":null},
    {"id":2,"estado":"FALLIDO","resultado":"FALLIDO","mensaje":null},
    {"id":3,"estado":"PROCESADO","resultado":"OMITIDO","mensaje":"El cobro ya estaba en un estado terminal"},
    {"id":999999,"estado":null,"resultado":"NO_ENCONTRADO","mensaje":"No existe el cobro indicado"}
  ]
}
```

### 4.6 Consultar cobros por cliente — `GET /clientes/{id}/cobros`

Filtros opcionales `estado`, `desde`, `hasta`. Las fechas aceptan instante ISO-8601
(`2026-01-31T23:59:59Z`) o fecha (`2026-01-31`, interpretada como inicio/fin del día UTC).

```bash
curl -s "$BASE/clientes/1/cobros?estado=PROCESADO&desde=2026-01-01&hasta=2026-12-31" -H "$AUTH"
```

### 4.7 Auditoría — `GET /auditoria`

Filtros opcionales `evento`, `desde`, `hasta` y `limite` (por defecto 100). Ordenado por fecha
descendente.

```bash
curl -s "$BASE/auditoria?evento=COBRO_PROCESADO&limite=50" -H "$AUTH"
```

### 4.8 Códigos de estado y formato de error

| Código | Cuándo |
|---|---|
| 200 | Consulta / procesamiento correcto |
| 201 | Recurso creado (`Location` incluido) |
| 400 | Validación de datos o parámetro de consulta inválido |
| 401 | Token ausente o inválido |
| 404 | Cliente o cobro no encontrado |
| 409 | DPI duplicado |
| 500 | Error inesperado (no filtra detalles internos) |

Formato uniforme de error:

```json
{
  "timestamp": "2026-11-18T00:29:51.587Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Datos inválidos",
  "path": "/cobros",
  "details": [{"field": "monto", "message": "El monto debe ser mayor a 0"}]
}
```

---

## 5. Reglas de negocio

- El **DPI** del cliente es único y debe tener 13 dígitos.
- Un **cobro** se registra en estado `PENDIENTE`; `monto > 0` y moneda ISO 4217 (3 letras).
- **Procesamiento:** `monto ≤ 1000 → PROCESADO`, `monto > 1000 → FALLIDO`.
- El **procesamiento por lotes es idempotente** (los cobros terminales se omiten).
- Toda acción relevante deja un **evento de auditoría** (`CLIENTE_CREADO`, `COBRO_REGISTRADO`,
  `COBRO_PROCESADO`, `COBRO_FALLIDO`, `LOTE_PROCESADO`).

---

## 6. Modelo de datos (SQLite)

| Tabla | Campos |
|---|---|
| `cliente` | `id`, `nombre`, `dpi` (único), `email`, `telefono` |
| `cobro` | `id`, `cliente_id`, `monto`, `moneda`, `estado`, `fecha_creacion`, `fecha_proceso`, `referencia_externa` (único) |
| `auditoria` | `id`, `evento`, `resumen_payload`, `fecha`, `usuario_sistema` |

El esquema lo genera Hibernate ORM al arrancar (`schema-management.strategy=update`; en tests
`drop-and-create`).

---

## 7. Pruebas

```bash
./gradlew test
```

51 pruebas, organizadas espejando la arquitectura:

- **Dominio (puro, sin frameworks):** `ReglaProcesamientoCobroTest`, `MontoTest`, `DpiTest`.
- **Casos de uso (Mockito):** `RegistrarCobroUseCaseTest`, `ProcesarCobroUseCaseTest`,
  `ProcesarLoteCobrosUseCaseTest`, `CrearClienteUseCaseTest`, `ConsultarAuditoriaUseCaseTest`.
- **Integración (`@QuarkusTest` + RestAssured):** `CobroResourceIT`, `ClienteResourceIT`,
  `AuditoriaResourceIT`, `ApiTokenFilterIT` — cubren 201/400/404/409/401, ambos caminos de
  procesamiento, idempotencia del lote, filtros por estado/fecha y trazabilidad.

---

## 8. Decisiones técnicas y trade-offs

1. **SQLite con driver directo (`db-kind=other`)** en lugar de la extensión Quarkiverse
   `quarkus-jdbc-sqlite` (última versión compilada contra Quarkus 3.15 → riesgo de
   incompatibilidad con 3.40). Se declaran driver (`org.xerial:sqlite-jdbc`) y dialecto
   (`hibernate-community-dialects`) explícitamente. *Trade-off:* Quarkus no puede validar la
   salud de la conexión (`Agroal ... db-kind other`) y no hay Dev Services.
2. **SQLite single-writer:** pool de 1 conexión (`jdbc.max-size=1`) + `journal_mode=WAL` y
   `busy_timeout` para evitar `SQLITE_BUSY`. *Trade-off:* poca concurrencia de escritura, más
   que suficiente para el demo.
3. **Esquema por Hibernate** (`update`/`drop-and-create`) en lugar de migraciones versionadas.
   Flyway no trae módulo SQLite en el BOM de Quarkus. *Trade-off:* para producción se usaría
   Flyway/Liquibase con SQL explícito.
4. **Onion estricta:** el modelo de dominio es puro (sin JPA/CDI) y existe una entidad JPA
   separada con *mappers* en `infrastructure`. *Trade-off:* más código, pero dominio testeable
   sin base de datos y puertos/adaptadores reales.
5. **Dependencias entre features:** `cobros.application` usa los **puertos de dominio** de
   `clientes` (`ClienteRepository`) y el **caso de uso** `auditoria.RegistrarEventoUseCase`.
   *Alternativa:* capa anticorrupción con puertos propios del consumidor.
6. **Token estático por cabecera** (`X-API-Token`) con comparación en tiempo constante.
   *Trade-off:* sencillo y suficiente para el alcance; en producción se recomienda JWT/OIDC.
   En el contenedor se **autogenera** y persiste en el volumen.
7. **Idempotencia del lote basada en estado terminal** (no en un idempotency-key por request).
   Reenviar el mismo lote devuelve el mismo resultado sin reprocesar.
8. **`@Transactional` en los casos de uso:** límite transaccional explícito en la capa de
   aplicación (excepción pragmática de Jakarta, no de dominio).
9. **FK `cobro.cliente_id` como columna simple** (sin relación JPA) para mantener el dominio
   desacoplado; la existencia del cliente se valida en el caso de uso.
10. **Build multi-stage en Docker:** etapa *builder* (JDK + Gradle wrapper) que compila el
    binario, y etapa *runtime* (JRE mínimo UBI). Así la imagen se construye desde el código
    fuente sin pasos previos. *Trade-off:* el build es más lento (descarga Gradle y
    dependencias) y no corre los tests (`-x test`); los tests se ejecutan con `./gradlew test`.

---

## 9. Estructura del repositorio

```
src/main/java/com/demo/cobros/
├── clientes/      domain · application · infrastructure
├── cobros/        domain · application · infrastructure
├── auditoria/     domain · application · infrastructure
└── shared/        security · logging · error · web
src/test/java/com/demo/cobros/   (espeja la estructura anterior)
src/main/docker/                 Dockerfile.jvm + docker-entrypoint.sh
REQUIREMENTS.md
```
