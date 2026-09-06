# Reliability in EDA — Outbox, Idempotency, Delivery Guarantees

---

## Delivery Guarantees

| Guarantee | Meaning | Trade-off |
|---|---|---|
| **At-most-once** | Event sent once; lost on failure. No retries. | No duplicates, but data loss possible |
| **At-least-once** | Event retried until acknowledged. Duplicates possible. | Safe against loss, consumer must be idempotent |
| **Exactly-once** | Event processed exactly once. | Complex, expensive; often simulated with idempotency |

**Reality:** Most production systems use **at-least-once + idempotent consumers**.
True exactly-once requires transactional guarantees that span producer, broker, and consumer — Kafka Transactions support this but add latency.

---

## The Dual Write Problem

Saving to DB and publishing an event are two separate operations — no atomic guarantee between them.

```java
// UNSAFE — classic dual write
orderRepository.save(order);         // DB commit succeeds
eventBus.publish(new OrderPlaced()); // crashes here → event lost, DB already updated
```

If the app crashes between the two, the DB has the update but the event was never published.

---

## Outbox Pattern (Transactional Outbox)

Solve dual write by writing the event to the **same database transaction** as the state change. A separate process reliably publishes it.

```
Step 1: One transaction writes both:
  ┌─ BEGIN ─────────────────────────────────────┐
  │  INSERT INTO orders ...                      │
  │  INSERT INTO outbox (event_type, payload)    │  ← same DB, same tx
  └─ COMMIT ─────────────────────────────────────┘

Step 2: Outbox publisher reads unpublished events and publishes to broker:
  SELECT * FROM outbox WHERE published = false
  → publish to Kafka/SQS/RabbitMQ
  → UPDATE outbox SET published = true
```

**Implementations:**
- **Polling publisher** — background thread/job polls outbox table periodically.
- **CDC (Change Data Capture)** — Debezium reads PostgreSQL WAL (write-ahead log) and streams inserts to Kafka. Zero polling, real-time. Preferred in production.

```yaml
# Debezium connector config (reads Postgres WAL → publishes to Kafka)
connector.class: io.debezium.connector.postgresql.PostgresConnector
database.hostname: postgres
database.dbname: mydb
table.include.list: public.outbox
```

**Guarantees:** At-least-once (event may be published twice if publisher crashes after publish but before marking done). Consumers must be idempotent.

---

## Idempotency — Handling Duplicate Events

An operation is idempotent if processing it multiple times produces the same result as processing it once.

### Why it's needed
- At-least-once delivery → duplicates happen.
- Network retries → same request received twice.
- Consumer restart → may re-read already-processed events.

### Idempotency key
Attach a unique ID to every event. Consumer deduplicates by tracking seen IDs.

```java
@KafkaListener(topics = "orders")
public void handle(OrderPlaced event) {
    if (processedEventRepo.existsById(event.eventId())) {
        return;  // already processed, skip
    }
    // ... process ...
    processedEventRepo.save(new ProcessedEvent(event.eventId()));
}
```

### DB-level idempotency
```sql
-- Upsert — safe to run multiple times
INSERT INTO order_status (order_id, status)
VALUES ('order-1', 'paid')
ON CONFLICT (order_id) DO NOTHING;
```

### Natural idempotency
Some operations are naturally idempotent:
- Setting a field to a value (`UPDATE ... SET status = 'shipped'`).
- Delete if exists.

Non-idempotent: incrementing a counter, appending to a list, sending an email.

---

## Dead Letter Queue (DLQ)

When a consumer fails to process a message after N retries, move it to a DLQ instead of losing it or blocking the queue.

```
Topic → Consumer → FAIL → Retry 1 → FAIL → Retry 2 → FAIL → DLQ
                                                               ↓
                                                        Alert + manual inspect
```

- DLQ keeps failed messages for inspection and reprocessing.
- Monitor DLQ depth — non-zero = something is broken.
- After fixing the bug, replay DLQ back to the original topic.

**Spring Kafka:**
```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<?, ?> template) {
    var recoverer = new DeadLetterPublishingRecoverer(template);
    var backoff = new FixedBackOff(1000L, 3);  // 3 retries, 1s apart
    return new DefaultErrorHandler(recoverer, backoff);
}
```

---

## Schema Evolution

Events are contracts. Consumers may be on older versions while you deploy new producers.

### Compatibility rules (Avro / Protobuf / JSON Schema)
| Type | Rule |
|---|---|
| **Backward compatible** | New schema can read data written with old schema. Add optional fields with defaults. |
| **Forward compatible** | Old schema can read data written with new schema. Don't remove fields old consumers use. |
| **Full compatible** | Both directions. Safest. Only add optional fields, never remove or change types. |

### Safe changes (backward + forward)
- Add an optional field with a default value.
- Rename: add new field + keep old one → deprecate old after all consumers migrate.

### Breaking changes (avoid)
- Remove a required field.
- Change field type (int → string).
- Rename without keeping the old name.

### Schema Registry
Centralized schema store (Confluent Schema Registry, AWS Glue Schema Registry).
Producer registers schema → gets schema ID. Consumer fetches schema by ID to deserialize.
Enforces compatibility rules at publish time — rejects breaking changes.

---

## Interview Points

- Dual write = the root cause of most EDA reliability bugs. Always use Outbox or CDC.
- Idempotency is not optional when using at-least-once delivery.
- Exactly-once is expensive — at-least-once + idempotent consumer is the pragmatic solution.
- DLQ + alert on non-zero depth is the minimum observability requirement.
- Schema evolution: version your events from day one. Never break consumers silently.
- Outbox polling adds latency (up to polling interval). CDC (Debezium) is near real-time.
