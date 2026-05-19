# PartFinder — Manual de demo y sustentación

Este documento sirve para dos cosas:

1. **Renderizar los diagramas C4** (PlantUML → PNG) para tener una imagen lista para entregar/mostrar.
2. **Guion de demo en vivo** paso a paso para mostrarle al profesor el funcionamiento del sistema durante la sustentación oral.

---

## Parte 1 — Renderizar los diagramas C4

Los diagramas viven en `docs/diagrams/c4/` como archivos `.puml` (PlantUML). Hay dos opciones para generar las imágenes PNG:

### Opción A — VSCode con extensión PlantUML (recomendada)

1. Instalar la extensión **"PlantUML"** de jebbs en VSCode (Ctrl+Shift+X → buscar "plantuml").
2. Abrir `docs/diagrams/c4/c4-l1-context.puml`.
3. `Alt+D` → muestra preview a la derecha.
4. Click derecho en el preview → **Export Current Diagram** → elegir **PNG**.
5. Repetir con `c4-l2-containers.puml`.
6. Los PNG quedan en la misma carpeta.

Requisitos: Java (ya está instalado en el sistema) + Graphviz. Si la extensión pide Graphviz, descargarlo de https://graphviz.org/download/ (en Windows: `winget install graphviz`).

### Opción B — PlantUML online (sin instalar nada)

1. Abrir https://www.plantuml.com/plantuml/uml/
2. Copiar el contenido de `c4-l1-context.puml` y pegarlo en el editor del sitio.
3. El diagrama se renderiza en vivo. Click derecho → **Guardar imagen como…** → `c4-l1-context.png`.
4. Repetir con `c4-l2-containers.puml`.

### Opción C — CLI (si tienes Java en PATH)

```bash
# Descargar plantuml.jar una vez
curl -L https://github.com/plantuml/plantuml/releases/latest/download/plantuml.jar -o plantuml.jar

# Generar PNGs
java -jar plantuml.jar docs/diagrams/c4/*.puml
```

### Donde colocar los PNG

Después de generarlos:
- `docs/diagrams/c4/c4-l1-context.png`
- `docs/diagrams/c4/c4-l2-containers.png`

El `README.md` ya los referencia desde la tabla de entregables.

---

## Parte 2 — Preparación antes de la sustentación

### Verificar que el stack esté arriba

```powershell
cd C:\dev\partfinder-marketplace
docker compose ps
```

Debes ver **12 contenedores** todos en `Up X (healthy)`. Si Docker Desktop está cerrado o algún contenedor faltó, arrancar:

```powershell
docker compose up -d
```

(sin `--build` — las imágenes ya están construidas; arranque en ~45 s).

### Abrir las pestañas del navegador en este orden

| # | URL | Para qué |
|---|---|---|
| 1 | http://localhost:8080 | **UI principal** — pasa el 80% del tiempo aquí |
| 2 | http://localhost:15672 | RabbitMQ Management (`partfinder` / `partfinder`) |
| 3 | http://localhost:16686 | Jaeger UI |
| 4 | http://localhost:3000 | Grafana — buscar dashboard "PartFinder Health" |
| 5 | http://localhost:8081/swagger-ui.html | Swagger Aggregator (por si pregunta contratos) |

Y abrir **PowerShell** en `C:\dev\partfinder-marketplace` para los comandos del Acto 4.

### Calentar el sistema (1 min antes de empezar)

Hacer 2-3 búsquedas exitosas para tener trazas frescas en Jaeger y métricas en Grafana:

```powershell
curl "http://localhost:8081/search?query=filtro&workshopId=WS-001"
curl "http://localhost:8081/search?query=bujias&workshopId=WS-001"
curl "http://localhost:8081/search?query=pastillas&workshopId=WS-001"
```

---

## Parte 3 — Guion de demo en vivo (~8 minutos)

### Acto 1 — Camino feliz (1 min)

**Pantalla:** UI en http://localhost:8080, pestaña **"Buscar parte"**.

1. Dejá `filtro` en el query y `WS-001` en el workshopId. Click **Buscar**.
2. Aparece: **PRT-FO-001 — Filtro de aceite Toyota Corolla 2018** con badge verde **AVAILABLE** y 3 proveedores con stock.

**Lo que dices al profe:**
> *"Esto es el camino feliz. El Aggregator buscó en Elasticsearch por full-text (con analyzer en español), luego consultó síncronamente a InventoryDirect via Feign para los 3 proveedores. La latencia es de ~50ms. La regla R1 no se disparó porque InventoryDirect respondió rápido."*

### Acto 2 — Demostrar R1 (Disponibilidad Incierta) (2 min)

**Pantalla:** pestaña **"Admin / simulación"**.

1. En "Slow mode · InventoryDirect", poner **1500 ms** y click **Aplicar**. Aparece "Delay actual: 1500 ms".
2. Volver a la pestaña **"Buscar parte"** y buscar `filtro` de nuevo.
3. Aparece: **PRT-FO-001** con badge ámbar **UNCERTAIN**. La latencia en pantalla muestra ~1200ms.

**Lo que dices al profe:**
> *"Esto es R1 en acción. Forzamos InventoryDirect a tardar 1500 ms. El bean `Request.Options` de Feign tiene `readTimeout=800ms`, así que la llamada lanza `SocketTimeoutException`. El `InventoryDirectFeignAdapter` la captura y devuelve `InventoryQueryResult(timedOut=true)`. El `SearchResultFactory` traduce eso a `Availability.UNCERTAIN`. **No bloqueamos al usuario** — le devolvemos la respuesta con la marca de incertidumbre."*

> *"Esto es diferente a un Circuit Breaker que rechaza. En PartFinder degradamos el contenido de la respuesta, no la disponibilidad del servicio. Es una elección comercial: el mecánico prefiere 'puede que lo tengan' antes que un error."*

4. Pestaña **"Admin"** otra vez, click **Reset (0)** para volver a la normalidad.

### Acto 3 — Demostrar R2 (Crédito excedido) (1 min)

**Pantalla:** pestaña **"Crear pedido"**.

1. Cambiar `workshopId` a **WS-003** (que en el seed tiene cupo excedido: límite 2.000.000, usado 2.200.000).
2. Dejar el resto (PRT-FO-001, SUP-LIMA, cantidad 1, precio 35000). Click **Crear pedido**.
3. Aparece banner rojo: **HTTP 422 — R2_CREDIT_EXCEEDED — Taller WS-003 ya tiene su cupo de credito excedido (usado=2200000.00 COP / limite=2000000.00 COP). R2 bloquea.**

**Lo que dices al profe:**
> *"Esto es R2. La regla vive en `domain/policy/OrderCreditPolicy.java` — Java puro, 40 líneas, sin Spring. Implementa la interfaz `OrderRulePolicy` (patrón Strategy GoF). El caso de uso `CreateOrderUseCase` recorre una lista de policies; si una falla, se aborta. Sumar una R nueva sería una clase más sin tocar nada."*

> *"El 'microservicio financiero' que menciona el enunciado lo materializamos como agregado dentro del Aggregator — la entidad `Workshop` trae `creditLimit` y `creditUsed`. Si en producción real existiera, escucharíamos sus eventos por broker y actualizaríamos la misma proyección local."*

4. Pestaña **"Admin"**, cambiar a `WS-002`, click **Cargar**. Muestra que usó 2.400.000 de 3.000.000 — al 80%. No bloquea.

### Acto 4 — Demostrar R3 (Cierre resiliente con Outbox) — **el más impactante** (2 min)

**Pantalla:** PowerShell + pestaña **"Tendencias"** de la UI.

1. **Pestaña "Buscar parte":** buscar `xyz-no-existe`. Devuelve `[]` y banner ámbar "Se publicó un SearchFailedEvent al broker".

**Lo que dices al profe:**
> *"R3 en marcha. La búsqueda no encontró nada en Elasticsearch. En la misma transacción JPA que cerró la búsqueda, el caso de uso escribió un `SearchFailedEvent` en la tabla `outbox_events` de MySQL. Esto es Transactional Outbox."*

2. **PowerShell:**
   ```powershell
   docker exec partfinder-mysql-aggregator mysql -uaggregator -paggregator -D aggregator -e "SELECT id, event_type, published_at FROM outbox_events ORDER BY id DESC LIMIT 3;"
   ```
   Mostrar que la fila tiene `published_at` con timestamp reciente.

> *"Un worker scheduleado cada 1 segundo recoge los eventos pendientes y los publica al broker. Acá ya pasó."*

3. **Pestaña "Tendencias"** de la UI, click **Refrescar**. Aparece `xyz-no-existe` con count 1.

> *"MS-TrendCollector consumió el evento (del exchange `partfinder.search.events` por la routing key `search.failed`) y lo persistió en PostgreSQL. La idempotencia está garantizada porque la tabla `failed_searches` tiene `event_id` como PRIMARY KEY — si el broker reintenta, el segundo INSERT colisiona."*

4. **(Bonus opcional si hay tiempo)** Demostrar que sigue funcionando con Rabbit caído:
   ```powershell
   docker compose stop rabbitmq
   # Buscar otra cosa en la UI que de NOT_FOUND, p.ej. "abc-tampoco-existe"
   docker exec partfinder-mysql-aggregator mysql -uaggregator -paggregator -D aggregator -e "SELECT id, published_at FROM outbox_events WHERE published_at IS NULL;"
   # -> aparece la fila con published_at NULL
   docker compose start rabbitmq
   # Esperar 30 segundos, volver a la consulta SQL — published_at ya tiene timestamp.
   ```

### Acto 5 — Observabilidad (2 min)

**Pestaña Jaeger** (http://localhost:16686):

1. Service: `ms-aggregator`. Operation: `GET /search`. Find Traces.
2. Click en cualquier traza. Mostrar el árbol de spans: `ms-aggregator → Feign HTTP GET → ms-inventory-direct → MySQL select`.

> *"Una sola petición HTTP, trazabilidad distribuida automática con OpenTelemetry. No escribimos código de tracing — el Java Agent lo inyecta. Cada span tiene el `trace_id` correlacionado, así que en un sistema real podríamos correlacionar logs, métricas y trazas para un debug end-to-end."*

**Pestaña Grafana** (http://localhost:3000):

1. Carpeta **PartFinder** → dashboard **PartFinder Health**.
2. Mostrar paneles: UP/DOWN por MS (3 en verde), HTTP RPS, **p95 latencia /search con threshold visible a 800ms**, JVM Heap, Rabbit ready/delivered.

> *"Métricas en tiempo real. Prometheus scrapea cada 15 segundos a `/actuator/prometheus` de los 3 MS más el plugin de RabbitMQ. Grafana lo visualiza. El panel del p95 tiene marcado el threshold de 800ms — si una corrida queda por encima, sabemos que R1 podría estar disparándose seguido."*

**Pestaña RabbitMQ** (http://localhost:15672, `partfinder/partfinder`):

1. Tab **Queues**. Mostrar `trends.search-failed.q` con `consumers=1`.
2. Tab **Exchanges**. Mostrar `partfinder.search.events` (topic).

> *"La topología la creó Spring AMQP al primer arranque vía `@Bean Queue/Exchange/Binding` en `RabbitMqConfig`. No usamos `load_definitions` porque RabbitMQ ignora `RABBITMQ_DEFAULT_USER/PASS` cuando se monta un definitions — fue una lección aprendida en VoltNet."*

### Cierre (30 seg)

> *"Resumen: 3 microservicios hexagonales, 4 patrones GoF (Strategy, Adapter, Factory, Observer), persistencia políglota dentro y entre MS (Elasticsearch + MySQL + PostgreSQL), comunicación síncrona con timeout determinístico (R1) y asíncrona con Outbox (R3), y observabilidad completa con métricas + trazas. Todo se levanta con un solo `docker compose up -d --build`. 38 tests unitarios verdes corriendo en menos de 10 segundos."*

---

## Parte 4 — Preguntas típicas del profe y respuestas armadas

### "¿Por qué Elasticsearch y no Postgres con tsvector?"
> *Búsqueda full-text con analyzer spanish (stemming + stopwords) está como ciudadano de primera en ES. Además ES devuelve scoring de relevancia que ordena los resultados, y agregaciones nativas que usaríamos si crece. Postgres con tsvector lo haría pero con más código y menor velocidad de iteración.*

### "¿No estás compartiendo BD si Aggregator usa ES + MySQL?"
> *No. La rúbrica dice "no se permite compartir esquemas o instancias entre servicios" — la prohibición es entre MS. Polyglot DENTRO de un MS está permitido. ES y MySQL son del Aggregator exclusivamente: InventoryDirect tiene su propio MySQL en otra instancia, TrendCollector tiene su propio PostgreSQL.*

### "¿Por qué Aggregator necesita dos stores?"
> *Elasticsearch no es transaccional. El patrón Outbox exige ACID: el evento se escribe en la misma transacción que la operación que lo origina. Por eso MySQL para el Outbox + proyección de crédito; ES para lo que realmente brilla, búsqueda full-text.*

### "¿Qué pasa si MySQL del Aggregator se cae?"
> *Spring Boot Actuator marca el `/actuator/health` como DOWN; el contenedor `(unhealthy)`; las búsquedas devuelven 500 hasta que vuelva. ES sigue arriba (independiente). Es el trade-off de partir la persistencia: un fallo afecta solo lo que necesita ese store.*

### "¿Por qué el timeout es exactamente 800 ms?"
> *Porque el enunciado lo dice literalmente. Lo configuramos en `FeignConfig.feignRequestOptions` con un `@Value("${aggregator-feign.read-timeout-ms:800}")` — es modificable sin recompilar.*

### "¿Por qué Strategy y no un if-else?"
> *Porque la rúbrica pide 3 patrones GoF demostrables. Strategy aplica aquí naturalmente: `CreateOrderUseCase` recibe una `List<OrderRulePolicy>` y las recorre. Si mañana sumamos R4 (límite diario por taller) es una clase nueva sin tocar el caso de uso — Open/Closed en vivo.*

### "¿Por qué no usaron Kafka?"
> *El volumen del caso no justifica la complejidad operacional de Kafka. RabbitMQ con DLQ y publisher confirms cubre exactly-once-effective. Kafka brilla cuando necesitás event sourcing, replays masivos o miles de mensajes/seg — no es el caso de PartFinder.*

### "¿Y si me das un caso donde el outbox no se publica nunca?"
> *El worker reintenta cada segundo. Si el broker cae 5 minutos, el evento queda con `published_at=NULL` y `attempts` se incrementa. Cuando el broker vuelve, en el siguiente tick se publica. Cero pérdida. Si el broker estuviera caído indefinidamente, el evento simplemente espera — el sistema converge al estado correcto cuando la dependencia vuelve.*

### "¿Cómo manejan corrupción de mensajes?"
> *DLQ. La cola `trends.search-failed.q` tiene `x-dead-letter-exchange` apuntando a `partfinder.trends.dlx`. Mensajes que el consumer rechaza (excepción no-recoverable, error de deserialización) caen a `trends.search-failed.dlq`. Los inspeccionamos manualmente; no bloquean la cola principal.*

### "Si el profe ve la pestaña UI 'Tendencias' y dice '¿por qué TrendCollector tiene un endpoint REST si dijiste que era 100% async?'"
> *El endpoint `GET /trends/top` es de consulta INTERNA — está solo para que la UI lo lea y para que Swagger demuestre que el MS tiene contrato documentado (un mínimo de rúbrica). Ningún otro MS lo invoca. El flujo de eventos entre MS es 100% async. Esta separación entre 'API de lectura para humanos' y 'integración entre MS' es buena práctica.*

---

## Parte 5 — División de roles si quieren ensayar entre los 5

Cada uno defenderá todo, pero pueden dividir el ENSAYO por dominio de profundidad:

| Persona | Área a profundizar |
|---|---|
| 1 | Caso de negocio + las 3 reglas R1/R2/R3 + decisiones (Acto 1-4) |
| 2 | Hexagonal + SOLID + por qué cada decisión arquitectónica |
| 3 | Los 4 patrones GoF con código en pantalla |
| 4 | Comunicación sync (Feign + R4J) vs async (RabbitMQ + Outbox) |
| 5 | Persistencia políglota + observabilidad (Prom + Grafana + Jaeger) |

Todos deben poder responder las preguntas de Parte 4 — ese es el mínimo común.

---

## Parte 6 — Troubleshooting durante la demo

| Síntoma | Causa probable | Fix rápido |
|---|---|---|
| `docker compose ps` muestra algún (unhealthy) | Está arrancando todavía | Esperar 30s y `docker compose ps` otra vez |
| UI da "ERR_CONNECTION_REFUSED" en http://localhost:8080 | UI Gateway aún starting | `docker compose logs ui-gateway` |
| Búsqueda devuelve `[]` para términos que existen | ES no terminó de indexar el seed | Esperar 30s más; `curl http://localhost:9200/parts/_count` debe dar 5 |
| Demo R1: el delay no se aplica | Olvidé hacer click en "Aplicar" en Admin | Verificar "Delay actual" muestra el valor nuevo |
| Demo R2: WS-003 da 201 en vez de 422 | Alguien lo modificó en demos anteriores | `POST /api/aggregator/admin/workshops/WS-003/credit-used {"amount":2200000}` para resetear |
| Demo R3: no aparece en /trends | Esperar 3-5 segundos; el worker corre cada 1s + Rabbit puede tener latencia | Refrescar pestaña Tendencias |
| Jaeger no muestra trazas | Las trazas tardan ~10s en aparecer | Esperar y refrescar; hacer otra búsqueda nueva primero |

---

## Parte 7 — Después de la sustentación, apagar limpio

```powershell
cd C:\dev\partfinder-marketplace
docker compose stop
```

(No usar `docker compose down -v` — borra los datos del seed y mañana tarda en re-poblar.)
