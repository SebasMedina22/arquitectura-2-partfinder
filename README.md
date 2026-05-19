# PartFinder — El Marketplace de Repuestos

> Proyecto integrador — **Arquitectura de Software II** (2026-1)
> Caso de estudio 7: **PartFinder — El Marketplace de Repuestos**

PartFinder conecta talleres mecánicos con cientos de bodegas de repuestos. Orquesta búsquedas a través de proveedores con sistemas heterogéneos (algunos lentos), bloquea pedidos de talleres con crédito excedido, y registra asíncronamente las búsquedas fallidas para que los proveedores ajusten su inventario futuro.

---

## Arquitectura en una imagen

```
                  ┌──────────────┐
                  │   Nginx GW   │  ← punto de entrada único + UI estática
                  └──────┬───────┘
                         │
        ┌────────────────▼────────────────┐
        │       MS-Aggregator             │  Core hexagonal (Spring Boot)
        │   Elasticsearch · puerto 8081   │
        └──┬──────────────────┬───────────┘
           │ REST sync         │ async (RabbitMQ)
           │ (timeout 800ms)   │
           ▼                   ▼
   ┌──────────────────┐   ┌────────────────────┐
   │ MS-InventoryDir. │   │  MS-TrendCollector │
   │ MySQL · 8082     │   │  PostgreSQL · 8083 │
   └──────────────────┘   └────────────────────┘
```

---

## Tabla de entregables

| Entregable | Ubicación |
|---|---|
| Documento RFC (PDF) | [`docs/RFC.md`](docs/RFC.md) → exportar a PDF |
| Diagrama C4 Nivel 1 (Contexto) | `docs/diagrams/c4/c4-l1-context.puml` |
| Diagrama C4 Nivel 2 (Contenedores) | `docs/diagrams/c4/c4-l2-containers.puml` |
| MS-Aggregator (código) | `services/ms-aggregator/` |
| MS-InventoryDirect (código) | `services/ms-inventory-direct/` |
| MS-TrendCollector (código) | `services/ms-trend-collector/` |
| Docker Compose | `docker-compose.yml` |
| Nginx Gateway + UI (bonus) | `ui/` |
| Prometheus + Grafana + Jaeger | `infra/` |

---

## Las 3 reglas de negocio

| Regla | Qué hace | Dónde vive |
|---|---|---|
| **R1** — Disponibilidad incierta por timeout | Si MS-InventoryDirect tarda > 800ms, el resultado se marca como `UNCERTAIN` (no se bloquea al usuario, se le devuelve el aviso). | `services/ms-aggregator/src/main/java/com/partfinder/aggregator/infrastructure/client/InventoryDirectFeignAdapter.java` (Adapter GoF + `@TimeLimiter` Resilience4j) |
| **R2** — Bloqueo de pedido por crédito excedido | Si el taller tiene cupo de crédito excedido, no se procesa el pedido (HTTP 422). | `services/ms-aggregator/src/main/java/com/partfinder/aggregator/domain/policy/OrderCreditPolicy.java` (Strategy GoF) |
| **R3** — Notificación asíncrona de búsquedas fallidas | Cada búsqueda que devuelve NOT_FOUND publica un evento al broker (vía Outbox); TrendCollector lo consume para acumular tendencias por proveedor. | `services/ms-aggregator/src/main/java/com/partfinder/aggregator/infrastructure/messaging/OutboxRelayWorker.java` (Observer GoF + Transactional Outbox) |

---

## Cómo levantar todo

> Requisitos: Docker Desktop corriendo. Nada más.

```bash
docker compose up -d --build
```

El primer build tarda ~4-5 min (descarga imágenes + compila los 3 servicios). Después arranca en segundos. Verifica con `docker compose ps` que los contenedores estén `Up (healthy)`.

URLs principales tras levantar (a confirmar al cierre de Fase 5):

| Servicio | URL | Credenciales |
|---|---|---|
| **UI (frontend) + API Gateway Nginx** | **http://localhost:8080** | — |
| MS-Aggregator Swagger | http://localhost:8081/swagger-ui.html | — |
| MS-InventoryDirect Swagger | http://localhost:8082/swagger-ui.html | — |
| MS-TrendCollector Swagger | http://localhost:8083/swagger-ui.html | — |
| Elasticsearch | http://localhost:9200 | — |
| RabbitMQ Management | http://localhost:15672 | partfinder / partfinder |
| Prometheus | http://localhost:9090 | — |
| Grafana | http://localhost:3000 | admin / admin |
| Jaeger | http://localhost:16686 | — |

---

## Stack tecnológico

| Capa | Tecnología | Por qué |
|---|---|---|
| Lenguaje | Java 25 LTS | LTS soportada por Spring Boot 3.5+ |
| Framework | Spring Boot 3.5 | Estándar de facto para microservicios JVM |
| Comunicación síncrona | OpenFeign + Resilience4j (TimeLimiter) | Cliente REST declarativo con control fino de timeout para R1 |
| Comunicación asíncrona | RabbitMQ + Spring AMQP | Más simple que Kafka, throughput suficiente |
| Persistencia búsqueda | Elasticsearch 8 (Aggregator) | Búsqueda full-text + agregaciones nativas |
| Persistencia transaccional | MySQL 8 (InventoryDirect, Aggregator outbox), PostgreSQL 16 (TrendCollector) | ACID donde se necesita |
| Observabilidad | Prometheus + Grafana + Jaeger + OpenTelemetry | Stack abierto estándar |
| Gateway + UI | Nginx 1.27 (mismo container) + React 18 + Vite | Bonus de la rúbrica |

Para la justificación de cada decisión y el análisis de trade-offs, ver `docs/RFC.md`.

---

## Equipo

| Integrante | Rol |
|---|---|
| Sebastián Medina | _por definir_ |
| _Pendiente_ | _por definir_ |

---

## Estado del proyecto

🚧 **En construcción** — ver `docs/RFC.md` para el diseño detallado y `docs/ARCHITECTURE.md` para la explicación pedagógica.
