# Event Sourcing & CQRS

---

## Event Sourcing

### Core idea
Instead of storing the **current state** of an entity, store the **sequence of events** that led to it.
The current state is always derived by replaying events.

```
Traditional DB:           Event Store:
orders table              orders_events table
─────────────             ──────────────────────────────────────────────
id | status | total       id | aggregate_id | type            | payload
1  | shipped | 99         1  | order-1      | OrderCreated    | {items, total:99}
                          2  | order-1      | PaymentReceived | {method:"card"}
                          3  | order-1      | OrderShipped    | {trackingId:"DHL123"}
```

### Key terms
| Term | Meaning |
|---|---|
| **Aggregate** | The entity whose state is built from events (e.g. Order, Account) |
| **Event** | Immutable fact describing what happened (past tense). Never deleted. |
| **Event Store** | Append-only log of events, ordered per aggregate |
| **Snapshot** | Periodic save of current state to avoid replaying all history (optimization) |
| **Projection** | A read model built by processing events (one aggregate = one projection usually) |
| **Replay** | Rebuild state from scratch by reprocessing all events |

### Java pattern
```java
public class Order {
    private String id;
    private OrderStatus status;
    private List<OrderItem> items;
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    // Apply is the only way to mutate state — called during replay AND new commands
    public void apply(OrderCreated event) {
        this.id = event.orderId();
        this.items = event.items();
        this.status = OrderStatus.PENDING;
    }
    public void apply(OrderShipped event) {
        this.status = OrderStatus.SHIPPED;
    }

    // Command handler — validates, then applies
    public void ship(String trackingId) {
        if (status != OrderStatus.PAID) throw new IllegalStateException("Not paid");
        var event = new OrderShipped(id, trackingId, Instant.now());
        apply(event);
        uncommittedEvents.add(event);   // persisted by repository
    }

    // Rebuild from event history
    public static Order reconstitute(List<DomainEvent> history) {
        var order = new Order();
        history.forEach(e -> {
            if (e instanceof OrderCreated c) order.apply(c);
            else if (e instanceof OrderShipped s) order.apply(s);
            // ...
        });
        return order;
    }
}
```

### Benefits
- Complete audit trail — every state change is recorded.
- Time travel — replay events to any point in time.
- Debug by re-running history.
- Event log naturally feeds CQRS projections and other services.

### Drawbacks
- Querying current state requires a projection (can't `SELECT * WHERE status = 'shipped'` directly).
- Event schema changes require migration strategy (versioning, upcasting).
- Eventual consistency between write model and read projections.
- Snapshot management adds complexity for high-volume aggregates.

---

## CQRS — Command Query Responsibility Segregation

### Core idea
Split the model into two:
- **Write side (Command)** — handles state-changing operations, validates, emits events. Optimized for writes and consistency.
- **Read side (Query)** — separate, denormalized read models. Optimized for specific queries.

```
                    ┌─────────────┐
Client ──command──► │ Command side│ ──event──► Event Bus ──► Projector ──► Read DB
       ◄──query───  │             │                                         (denormalized)
                    │  Write DB   │
                    └─────────────┘
```

### Why separate them?
- Write model normalizes for integrity; read model denormalizes for speed.
- Read traffic (often 10-100x writes) can scale independently.
- Each query can have its own optimized read model (e.g., Elasticsearch for search, Redis for leaderboard, PostgreSQL for reporting).

### CQRS without Event Sourcing
CQRS and Event Sourcing are independent — you can use either without the other:
- **CQRS only** — separate read/write endpoints with a sync mechanism (DB replication, change data capture).
- **Event Sourcing only** — single model reading from event replay.
- **Both** — the most common combination in DDD/microservices.

### Spring example (simplified)
```java
// Command side
@CommandHandler
public void handle(PlaceOrderCommand cmd) {
    var order = new Order(cmd.orderId(), cmd.items());
    orderRepository.save(order);                    // saves events
    eventBus.publish(new OrderPlaced(cmd.orderId())); // fans out
}

// Read side projection (updated asynchronously)
@EventHandler
public void on(OrderPlaced event) {
    var view = new OrderSummaryView(event.orderId(), "PENDING", event.total());
    orderSummaryRepo.save(view);                    // denormalized read table
}

// Query
@QueryHandler
public OrderSummaryView handle(GetOrderSummaryQuery query) {
    return orderSummaryRepo.findById(query.orderId());   // cheap read from view
}
```

### Interview points
- CQRS ≠ Event Sourcing. Don't conflate them.
- Read models are eventually consistent — lag between write and read can be milliseconds to seconds.
- CQRS adds complexity — justify it with actual read/write asymmetry. Don't apply to simple CRUD.
- Frameworks: Axon Framework (Java, full ES+CQRS), Spring Modulith, custom with Kafka.
