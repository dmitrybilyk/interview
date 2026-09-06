# Event-Driven Architecture — Interview Guide

---

## [01 — Fundamentals](_01_eda_fundamentals.md)

### What is EDA
Components communicate via **events** — not direct calls. Publishers don't know who listens; consumers don't know who produced.

### Event vs Command vs Message
| | Event | Command |
|---|---|---|
| Meaning | "Something happened" (fact, past tense) | "Do this" (imperative, directed) |
| Direction | Broadcast (no specific receiver) | Targeted (one receiver) |
| Example | `OrderPlaced`, `PaymentFailed` | `ProcessPayment`, `SendEmail` |

### Three EDA Patterns (Martin Fowler)

| Pattern | How | Trade-off |
|---|---|---|
| **Event Notification** | Lightweight signal only; consumer calls back to get data | Small events, but extra HTTP call + coupling back to producer API |
| **Event-Carried State Transfer** | Event contains all data consumer needs | Autonomous consumers; larger events, consumer manages its own cache |
| **Event Sourcing** | Events ARE the source of truth; state derived by replay | Full audit + time travel; complex query model |

### Choreography vs Orchestration

**Choreography** — services react to events, chain themselves:
```
OrderCreated → PaymentService pays → PaymentCompleted → ShippingService ships
```
Pro: fully decoupled. Con: flow is implicit, hard to trace.

**Orchestration** — central coordinator drives every step with commands:
```
OrderOrchestrator → cmd:ProcessPayment → cmd:ReserveInventory → cmd:Ship
```
Pro: visible workflow, explicit compensation. Con: coordinator is a coupling point.

Rule: complex flows with compensations → **Orchestration**. Simple fanout → **Choreography**.

### Benefits / Drawbacks
| Benefits | Drawbacks |
|---|---|
| Loose coupling, independent scaling | Eventual consistency (not immediate) |
| Resilience (broker buffers events) | Harder to debug and trace |
| Natural audit trail | Duplicate events — consumers must be idempotent |
| Easy extensibility (add consumer without touching producer) | Schema evolution discipline required |

---

## [02 — Event Sourcing & CQRS](_02_event_sourcing_cqrs.md)

### Event Sourcing
Store the **sequence of events**, not current state. State = replay of all events.

```
EventStore: [AccountOpened] [MoneyDeposited +100] [MoneyWithdrawn -30]
Balance = 0 + 100 - 30 = 70
```

Key terms:
- **Aggregate** — entity whose state is built from events (Order, Account).
- **Event** — immutable fact. Never deleted. Past tense.
- **Snapshot** — periodic state save to avoid full replay (optimization).
- **Projection** — read model derived by processing events.
- **Replay** — rebuild any state by reprocessing events from the log.

Benefits: full audit trail, time travel, events naturally feed other services.
Drawbacks: no direct query of current state (need projections), schema evolution complexity.

### CQRS — Command Query Responsibility Segregation
Split write model (normalized, for integrity) from read model (denormalized, optimized per query).

```
Client ──command──► Write side ──event──► Projector ──► Read DB (denormalized)
       ◄──query──────────────────────────────────────────────────────────────
```

Why:
- Read traffic (10-100x writes) scales independently.
- Each query can have its own optimized store (Elasticsearch, Redis, PostgreSQL).
- Write model optimizes for consistency; read model optimizes for speed.

CQRS ≠ Event Sourcing — they're independent. You can use either without the other.

Java/Spring: Axon Framework provides full ES + CQRS support out of the box.

---

## [03 — Saga Pattern](_03_saga.md)

### The problem
Distributed transactions span multiple services. No global 2PC in microservices.
Solution: a **sequence of local transactions** with **compensating transactions** on failure.

```
T1 (reserve stock) → T2 (charge payment) → T3 (schedule shipping)
                                          ← FAIL
                    C2 (refund payment)   ← compensate
C1 (release stock) ←
```

### Choreography Saga
Each service emits events; next service listens and reacts. Compensation also via events.
```
StockReserved → payment charged → PaymentCompleted → shipping starts
PaymentFailed → release stock → StockReleased → cancel order
```

### Orchestration Saga
```java
@StartSaga
public void on(OrderCreated e) { commandGateway.send(new ReserveStockCommand(...)); }

public void on(StockReserved e) { commandGateway.send(new ChargePaymentCommand(...)); }

public void on(PaymentFailed e) { commandGateway.send(new ReleaseStockCommand(...)); } // compensate
```

### Rules for compensating transactions
1. Must be **idempotent** — safe to retry.
2. Execute in **reverse order**.
3. Cannot always fully undo (email sent → send cancellation, not unsend).
4. Saga state must be **durable** — survives coordinator restart.

Frameworks: **Axon Framework** (Java), Temporal, Conductor (Netflix), AWS Step Functions.

---

## [04 — Reliability](_04_reliability.md)

### Delivery Guarantees
| Guarantee | Risk | Solution |
|---|---|---|
| At-most-once | Data loss | — |
| **At-least-once** | Duplicates | Idempotent consumers (pragmatic choice) |
| Exactly-once | Complex, expensive | Kafka Transactions (use sparingly) |

### Dual Write Problem
Saving to DB and publishing an event are two separate operations — no atomicity between them.
```java
// UNSAFE:
orderRepo.save(order);          // succeeds
eventBus.publish(OrderPlaced)   // crash here → event lost, DB already changed
```

### Outbox Pattern (fix for dual write)
Write event to the **same transaction** as the state change. Separate process publishes it.
```sql
-- One transaction:
INSERT INTO orders ...
INSERT INTO outbox (event_type, payload) VALUES ('OrderPlaced', '{"orderId":1}')
```
Publisher reads outbox → publishes to broker → marks as published.

- **Polling publisher** — background job polls outbox. Simple, adds latency.
- **CDC (Debezium)** — reads PostgreSQL WAL, streams to Kafka. Real-time, production grade.

### Idempotency
Track processed event IDs — skip if already seen.
```java
if (processedEventRepo.existsById(event.eventId())) return; // skip duplicate
// ... process ...
processedEventRepo.save(new ProcessedEvent(event.eventId()));
```
Or use DB upsert: `INSERT ... ON CONFLICT DO NOTHING`.

Natural idempotency: setting a field to a value, delete if exists.
Non-idempotent: incrementing a counter, sending an email → need explicit dedup.

### Dead Letter Queue (DLQ)
After N retry failures, move message to DLQ instead of blocking the queue.
```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<?, ?> t) {
    return new DefaultErrorHandler(
        new DeadLetterPublishingRecoverer(t),
        new FixedBackOff(1000L, 3));   // 3 retries, 1s apart
}
```
Monitor DLQ depth — non-zero = something is broken. Replay after fixing.

### Schema Evolution
| Change | Safe? |
|---|---|
| Add optional field with default | Yes |
| Remove unused field | No — breaks old consumers reading new events |
| Change field type | No |
| Rename field | No (keep old + add new, deprecate gradually) |

Use a **Schema Registry** (Confluent / AWS Glue) to enforce compatibility at publish time.

---

## Key Interview One-Liners

- **Event vs Command**: events are facts (past tense, broadcast); commands are directives (imperative, targeted).
- **Outbox pattern** = the correct solution to dual write. CDC (Debezium) is the production-grade implementation.
- **At-least-once + idempotent consumer** = pragmatic exactly-once without Kafka Transactions complexity.
- **Saga** = eventual consistency alternative to 2PC distributed transactions. Choreography for simplicity, Orchestration for complex flows.
- **CQRS ≠ Event Sourcing** — independent patterns, often combined.
- **Event Sourcing** is overkill for most services — use it when full audit trail or time travel is a genuine requirement.
- **Eventual consistency** is not a bug — design the UI to show processing states, not immediate confirmation.
- **DLQ depth > 0** = alerting must fire. It means messages are failing silently.
