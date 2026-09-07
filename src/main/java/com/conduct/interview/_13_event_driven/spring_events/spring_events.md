# Spring Application Events

## What is it?

Spring's built-in **in-process event bus**. Beans publish events; other beans listen.
No broker, no network, no infrastructure — everything happens inside one JVM, within the same Spring context.

It is not a distributed messaging system. It is a tool for **decoupling layers within a single service**:
the same way Kafka decouples services, Spring Events decouples components inside one service.

```
OrderService                 Spring Event Bus              Listeners
  publisher.publishEvent() ──→ ApplicationEventMulticaster ──→ InventoryListener
                                                           ──→ AuditListener
                                                           ──→ EmailListener (async)
```

**When to use:**
- Side effects that don't belong in the core flow (audit log, cache eviction, metrics).
- Avoiding direct bean-to-bean dependencies across modules.
- Triggering external calls only after a DB transaction commits (`@TransactionalEventListener`).

**Not a replacement for Kafka/RabbitMQ:** events are in-memory, not persistent. If the app restarts
or the thread dies, the event is gone.

Run `SpringEventsApp` — no docker needed.

---

## How It Works

```
OrderService
  publisher.publishEvent(new OrderPlaced(...))
       ↓
  Spring ApplicationEventMulticaster
       ↓
  @EventListener methods (all matching listeners called)
```

Any Spring bean can publish (`ApplicationEventPublisher`) or listen (`@EventListener`).
Multiple listeners on the same event are all called — order by `@Order` if needed.

---

## Three Listener Styles

### 1. `@EventListener` — synchronous, same thread

```java
@EventListener
public void onOrderPlaced(OrderEvents.OrderPlaced event) {
    // runs in the publisher's thread, before publishEvent() returns
}
```

- If this throws → exception propagates to the publisher.
- All sync listeners finish before `publishEvent()` returns.
- Use when: the side effect must complete before the calling method continues (validation, cache update).

### 2. `@Async @EventListener` — background thread

```java
@Async
@EventListener
public void onOrderShipped(OrderEvents.OrderShipped event) {
    // runs in Spring's async thread pool
    // publisher returns immediately — doesn't wait
}
```

- Requires `@EnableAsync` on a config class.
- If this throws → exception is logged, NOT propagated to the publisher.
- Use when: slow or non-critical side effects (email, push notification, analytics).

### 3. `@TransactionalEventListener` — tied to transaction outcome

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onOrderCancelled(OrderEvents.OrderCancelled event) {
    // runs ONLY if the publishing transaction committed successfully
    // NOT called if the transaction rolled back
}
```

| Phase | When |
|---|---|
| `AFTER_COMMIT` | Default. Transaction committed. Safe to call external APIs. |
| `BEFORE_COMMIT` | Still in transaction. Throwing here **rolls back** the tx. |
| `AFTER_ROLLBACK` | TX failed. Use for compensation / cleanup. |
| `AFTER_COMPLETION` | Always — commit or rollback. |

**Why this matters:** Solves the dual-write problem **within the same JVM**.
Without `@TransactionalEventListener`, a sync listener could send an email even if the DB transaction
later rolls back — email sent for an order that was never saved.

```java
// With @TransactionalEventListener(AFTER_COMMIT):
@Transactional
public void cancelOrder(String id) {
    orderRepo.save(order.cancel());         // DB write
    publisher.publishEvent(new Cancelled(id)); // event held until commit
}
// Listener fires only after DB commit — no stale side effects.
```

---

## Event Types

**Classic** (extends `ApplicationEvent`):
```java
public class OrderPlaced extends ApplicationEvent {
    public final String orderId;
    public OrderPlaced(Object source, String orderId) { super(source); this.orderId = orderId; }
}
```

**Modern POJO** (Spring 4.2+, preferred):
```java
public record OrderShipped(String orderId, String trackingNumber) {}

@EventListener
public void handle(OrderShipped event) { ... }  // just works
```

---

## When to Use Spring Events vs a Broker

| Criteria | Spring Events | Kafka / RabbitMQ |
|---|---|---|
| Scope | Same JVM only | Cross-service |
| Persistence | No — lost on restart | Yes |
| Guaranteed delivery | No | Yes (with acks) |
| Setup | Zero | Broker infrastructure |
| Ordering | Listener registration order | Kafka: per-partition |
| Use case | In-service decoupling | Cross-service communication |

**Rule:** Use Spring Events to decouple layers within one service. Use a broker when
different services (separate deployments) need to communicate.

---

## Interview Points

- `@TransactionalEventListener(AFTER_COMMIT)` is the in-process version of the Outbox pattern.
- Async listeners require `@EnableAsync`; exceptions are swallowed — log them with `AsyncUncaughtExceptionHandler`.
- Multiple listeners on the same event: use `@Order` to control sequence if it matters.
- `publishEvent()` with sync listeners is blocking — a slow listener slows the whole request thread.
- Spring Events are not durable: if the app restarts mid-processing, the event is lost.
