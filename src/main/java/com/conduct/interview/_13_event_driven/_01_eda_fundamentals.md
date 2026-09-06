# Event-Driven Architecture — Fundamentals

## What is EDA
Components communicate by producing and consuming **events** rather than calling each other directly.
Publishers don't know who listens. Consumers don't know who produced. Decoupled by design.

```
Monolith (tight coupling):       EDA (loose coupling):
OrderService.call(PaymentService)   OrderService → [event: OrderPlaced] → broker
                                        ↓               ↓               ↓
                                    PaymentService  ShippingService  EmailService
```

## Event vs Command vs Message

| | Event | Command | Message |
|---|---|---|---|
| Meaning | "Something happened" (fact, past tense) | "Do this" (imperative, directed) | Umbrella term |
| Direction | Broadcast (no specific receiver) | Targeted (one receiver) | Either |
| Example | `OrderPlaced`, `PaymentFailed` | `ProcessPayment`, `SendEmail` | Either |
| Response | None expected | May expect result | Depends |

## Three EDA Patterns (Martin Fowler)

### 1. Event Notification
Lightweight ping — just signals something happened. Consumer calls back to get details.
```
Order service → [OrderPlaced {orderId: 123}] → Inventory service
Inventory service → GET /orders/123 (fetches full order)
```
- **Pro:** Small events, easy schema. Producer owns the data.
- **Con:** Extra HTTP call on every event. Tight coupling back to producer's API.

### 2. Event-Carried State Transfer
Event contains all the data the consumer needs — no callback required.
```
[OrderPlaced {orderId:123, userId:42, items:[...], total:99.99, address:{...}}]
```
- **Pro:** Consumers are fully autonomous. No dependency on producer at query time.
- **Con:** Larger events. Consumer caches its own read model (must handle updates).
- **Use when:** High read throughput, consumer needs to work offline, read model optimization.

### 3. Event Sourcing
The state of an entity is derived by replaying its event history. Events are the source of truth — not a current-state table.
```
EventStore: [AccountOpened] [MoneyDeposited +100] [MoneyWithdrawn -30] [MoneyDeposited +50]
Current balance = 0 + 100 - 30 + 50 = 120
```
See `_02_event_sourcing_cqrs.md` for full detail.

## Choreography vs Orchestration

### Choreography (decentralized)
Each service listens for events and decides what to do next. No central coordinator.
```
OrderService → OrderPlaced → PaymentService → PaymentCompleted → ShippingService → OrderShipped
```
- **Pro:** Fully decoupled. Easy to add new consumers without changing producers.
- **Con:** Flow is implicit — hard to see the big picture. Debugging requires tracing across services.

### Orchestration (centralized)
A coordinator (Saga orchestrator / process manager) drives the workflow explicitly.
```
OrderOrchestrator → cmd: ProcessPayment → PaymentService
                  ← event: PaymentCompleted
                  → cmd: ReserveInventory → InventoryService
                  ← event: InventoryReserved
                  → cmd: Ship → ShippingService
```
- **Pro:** Workflow is visible in one place. Easy to add compensating logic.
- **Con:** Orchestrator is a central coupling point. Becomes a bottleneck at scale.

**Rule of thumb:**
- Few services, complex workflow with compensations → **Orchestration**.
- Many independent consumers, simple reactions → **Choreography**.

## Benefits of EDA
- **Loose coupling** — services evolve independently.
- **Scalability** — consumers scale independently of producers.
- **Resilience** — broker buffers events; consumers process when ready.
- **Extensibility** — add a new consumer without touching producers.
- **Audit trail** — event log is a natural history of what happened.

## Drawbacks of EDA
- **Eventual consistency** — state across services converges, not immediate.
- **Complexity** — harder to debug, trace, and reason about flow.
- **Duplicate events** — at-least-once delivery means consumers must be idempotent.
- **Schema evolution** — event format changes must be backward-compatible.
- **Ordering** — global ordering across partitions/topics is hard.
