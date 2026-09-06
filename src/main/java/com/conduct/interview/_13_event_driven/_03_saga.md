# Saga Pattern — Distributed Transactions

## The problem
A distributed transaction spans multiple services. There is no global 2PC (two-phase commit) in microservices — it's too slow and creates tight coupling.

```
PlaceOrder flow:
  1. Reserve inventory   (InventoryService)
  2. Charge payment      (PaymentService)
  3. Schedule shipping   (ShippingService)

If step 3 fails → must undo steps 1 and 2.
```

## What is a Saga
A sequence of **local transactions**, each in its own service. If a step fails, **compensating transactions** undo previous steps.

```
T1 → T2 → T3 → FAIL → C2 (compensate T2) → C1 (compensate T1)
```

Compensation ≠ rollback. It's a new transaction that semantically reverses the action (e.g., refund a charge, release reserved stock).

---

## Choreography-based Saga

Each service listens for events and triggers the next step. No central coordinator.

```
OrderService                InventoryService              PaymentService
────────────                ────────────────              ──────────────
PlaceOrder
  → [OrderCreated]
                            listen OrderCreated
                            reserve stock
                            → [StockReserved]
                                                          listen StockReserved
                                                          charge card
                                                          → [PaymentCompleted]
OrderService
  listen PaymentCompleted
  confirm order
  → [OrderConfirmed]

── FAILURE PATH ──
                                                          charge fails
                                                          → [PaymentFailed]
                            listen PaymentFailed
                            release stock
                            → [StockReleased]
OrderService
  listen StockReleased
  cancel order
```

**Pro:** No central coordinator. Services are fully decoupled.
**Con:** Flow is implicit — live in event handlers spread across services. Hard to visualize and debug.

---

## Orchestration-based Saga

A dedicated **Saga Orchestrator** (or Process Manager) drives every step. It sends commands and listens for responses.

```java
// Spring State Machine / Axon / custom implementation
@SagaEventHandler(associationProperty = "orderId")
public class OrderSaga {

    @StartSaga
    @SagaEventHandler
    public void on(OrderCreated event) {
        commandGateway.send(new ReserveStockCommand(event.orderId(), event.items()));
    }

    @SagaEventHandler
    public void on(StockReserved event) {
        commandGateway.send(new ChargePaymentCommand(event.orderId(), event.total()));
    }

    @SagaEventHandler
    public void on(PaymentCompleted event) {
        commandGateway.send(new ScheduleShippingCommand(event.orderId()));
    }

    @EndSaga
    @SagaEventHandler
    public void on(OrderShipped event) { /* saga complete */ }

    // ── Compensation ──────────────────────────────────────────
    @SagaEventHandler
    public void on(PaymentFailed event) {
        commandGateway.send(new ReleaseStockCommand(event.orderId()));
    }

    @EndSaga
    @SagaEventHandler
    public void on(StockReleased event) {
        commandGateway.send(new CancelOrderCommand(event.orderId()));
    }
}
```

**Pro:** Workflow is visible in one place. Compensation logic is explicit and centralized.
**Con:** Orchestrator becomes a coupling point. All services must integrate with it.

---

## Choreography vs Orchestration

| | Choreography | Orchestration |
|---|---|---|
| Coordination | Implicit (events chain together) | Explicit (orchestrator drives) |
| Visibility | Distributed across handlers | Centralized in one class |
| Coupling | Low — services only know events | Medium — services respond to commands from orchestrator |
| Debugging | Hard — need distributed tracing | Easier — state is in orchestrator |
| Best for | Simple flows, many independent consumers | Complex flows with compensations, business processes |

---

## Compensating Transactions — Design Rules

1. **Idempotent** — compensation must be safe to retry (release stock twice = same result).
2. **Cannot always fully undo** — e.g., if an email was sent, you can send a cancellation email, but not unsend.
3. **Order matters** — compensate in reverse order.
4. **Mark as failed, don't silently skip** — if compensation fails, alert and require human intervention.

---

## Interview Points
- Sagas achieve **eventual consistency**, not ACID. Design your UX around this (show "processing" state).
- A saga failure leaves the system in a **partially completed state** until compensation runs — this is normal.
- Idempotency keys are essential — retried compensation must not double-refund.
- Saga state must be durable — orchestrator crashes and restarts must resume from where it left off.
- Frameworks: Axon Framework (Java), Temporal, Conductor (Netflix), AWS Step Functions.
