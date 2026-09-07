# Observability

Three pillars: **Logs**, **Metrics**, **Traces**. Together they answer: what happened, how often, and why.

---

## Logs

Record of discrete events. Structured logs (JSON) are machine-parseable — queryable in ELK/Loki.

```java
// Bad: unstructured
log.info("Order processed for user " + userId + " amount " + amount);

// Good: structured (Logback + logstash-logback-encoder)
log.info("Order processed", kv("userId", userId), kv("amount", amount), kv("orderId", orderId));
// → {"level":"INFO","message":"Order processed","userId":"u1","amount":99.99,"orderId":"ord-123"}
```

**Log levels**: ERROR (needs human action) → WARN (unexpected but handled) → INFO (business events) → DEBUG (dev only, off in prod).

**Correlation ID**: unique ID per request, propagated through all services. Add to every log line.
```java
MDC.put("correlationId", UUID.randomUUID().toString());  // SLF4J MDC — auto-included in logs
```

**Stacks**: ELK (Elasticsearch + Logstash + Kibana) or Grafana Loki (lighter, log aggregation).

---

## Metrics

Numerical measurements over time. Answer: how many, how fast, how much.

**Four golden signals** (Google SRE):
| Signal | Meaning | Example metric |
|---|---|---|
| **Latency** | How long requests take | `http_request_duration_seconds` p99 |
| **Traffic** | How many requests | `http_requests_total` |
| **Errors** | How many fail | `http_requests_total{status="5xx"}` |
| **Saturation** | How full the system is | `jvm_memory_used_bytes`, CPU % |

**Spring Boot Actuator + Micrometer + Prometheus + Grafana**:
```yaml
management.endpoints.web.exposure.include: health,info,prometheus
management.metrics.export.prometheus.enabled: true
```

```java
Counter.builder("orders.created").tag("region", "eu").register(meterRegistry).increment();
Timer.builder("payment.duration").register(meterRegistry).record(() -> paymentService.charge(req));
```

Prometheus scrapes `/actuator/prometheus`. Grafana visualizes. Alert on p99 latency, error rate, saturation.

---

## Traces (Distributed Tracing)

Follow a request across multiple services. Each service adds a span. Spans form a trace tree.

```
TraceId: abc-123
  Span: api-gateway        [0ms → 250ms]
    Span: order-service    [5ms → 200ms]
      Span: db-query       [10ms → 80ms]
      Span: payment-call   [90ms → 190ms]
```

**OpenTelemetry** (standard): instrument once, export to any backend (Jaeger, Zipkin, Tempo).

```java
// Spring Boot auto-instruments with micrometer-tracing + OTLP exporter
// Just add dependency — no code changes needed for HTTP/DB/messaging spans
```

```yaml
management.tracing.sampling.probability: 1.0  # 100% in dev, 0.1 in prod
spring.application.name: order-service        # appears in trace
```

**Baggage**: key-value propagated across service boundaries (e.g., userId, tenantId).

---

## Health Checks

```yaml
management.endpoint.health.show-details: always
```

Spring Boot Actuator `/actuator/health` — aggregates component health (DB, Redis, Kafka).
Kubernetes uses this for liveness (restart if dead) and readiness (route traffic only if ready) probes.

```yaml
livenessProbe:
  httpGet: { path: /actuator/health/liveness, port: 8080 }
readinessProbe:
  httpGet: { path: /actuator/health/readiness, port: 8080 }
```

---

## Alerting

Alert on symptoms (user-visible), not causes (internal signals).

- **Error rate** > 1% for 5 minutes → page on-call.
- **p99 latency** > 2s for 5 minutes → page on-call.
- **DLQ depth** > 0 → alert (messages failing processing).
- Avoid alerting on CPU % alone — only matters if it causes latency/errors.

---

## Interview Points

- Correlation ID is the minimum — without it you can't trace a request across services.
- Metrics answer "is something wrong?", traces answer "where is it slow?", logs answer "what exactly happened?".
- Structured logging is non-negotiable in microservices — regex-parsing free text at scale is painful.
- Sample traces in production (10-20%) — 100% tracing is expensive at high throughput.
- `/actuator/health/liveness` vs `/actuator/health/readiness`: liveness = "am I alive?", readiness = "am I ready to serve traffic?". Never put DB health in liveness — one DB blip restarts all pods.
