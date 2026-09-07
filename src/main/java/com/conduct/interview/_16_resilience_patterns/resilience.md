# Resilience Patterns

Distributed systems fail. These patterns prevent one failing service from cascading to everything else.

---

## Circuit Breaker

Wraps a remote call. Monitors failures. Opens circuit when failure rate exceeds threshold — fast-fails
instead of waiting for timeout, giving the downstream time to recover.

```
CLOSED (normal) → failures > threshold → OPEN (fast-fail) → after wait → HALF-OPEN (trial) → success → CLOSED
                                                                                             → fail   → OPEN
```

```java
// Resilience4j
CircuitBreaker cb = CircuitBreaker.ofDefaults("paymentService");

Supplier<String> decorated = CircuitBreaker.decorateSupplier(cb, () -> paymentService.charge(req));

Try.ofSupplier(decorated)
   .recover(CallNotPermittedException.class, ex -> "circuit open, use fallback");
```

**Configuration:**
- `failureRateThreshold` — % failures to open (default 50%).
- `waitDurationInOpenState` — how long to wait before HALF-OPEN (default 60s).
- `slidingWindowSize` — how many calls to measure.

---

## Retry

Automatically retry failed calls with backoff. Use for transient failures (network blip, 503).

```java
Retry retry = Retry.of("db", RetryConfig.custom()
    .maxAttempts(3)
    .waitDuration(Duration.ofMillis(500))
    .retryExceptions(IOException.class)
    .ignoreExceptions(IllegalArgumentException.class)  // don't retry business errors
    .build());

Supplier<Order> decorated = Retry.decorateSupplier(retry, () -> orderRepo.find(id));
```

**With exponential backoff + jitter** (prevents thundering herd on recovery):
```java
IntervalFunction.ofExponentialRandomBackoff(500, 2.0, 0.5, 10_000)
```

**Do NOT retry**: non-idempotent writes without idempotency key, business validation errors (400s).

---

## Bulkhead

Isolates thread pools per dependency so one slow/failing service doesn't exhaust all threads.

```java
// Thread-pool bulkhead: separate pool per downstream
Bulkhead bh = Bulkhead.of("inventory", BulkheadConfig.custom()
    .maxConcurrentCalls(20)    // max parallel calls
    .maxWaitDuration(Duration.ofMillis(50))  // reject if pool full > 50ms
    .build());
```

Analogy: ship compartments — one flooded compartment doesn't sink the ship.

---

## Rate Limiter

Limits calls per time window — protects the downstream from being overwhelmed.

```java
RateLimiter rl = RateLimiter.of("sms", RateLimiterConfig.custom()
    .limitForPeriod(10)               // 10 calls
    .limitRefreshPeriod(Duration.ofSeconds(1))  // per second
    .timeoutDuration(Duration.ofMillis(25))     // wait up to 25ms for permit
    .build());
```

**Token bucket** (bursty allowed) vs **fixed window** (strict). Resilience4j uses token bucket.

---

## Timeout

Never let a call block forever. Always set a timeout.

```java
TimeLimiter tl = TimeLimiter.of(TimeLimiterConfig.custom()
    .timeoutDuration(Duration.ofSeconds(2))
    .build());

Future<String> future = pool.submit(() -> slowExternalCall());
tl.executeFutureSupplier(() -> future);  // throws TimeoutException after 2s
```

---

## Fallback

What to do when the call fails (after retries/circuit open). Degrade gracefully.

```java
// After circuit breaker / retry exhausted
Try.ofSupplier(decorated)
   .recover(ex -> cachedResult())        // stale cache
   .recover(ex -> defaultValue());       // static default
```

---

## Combining Patterns (correct order matters)

```
Request → RateLimiter → CircuitBreaker → Retry → Timeout → Bulkhead → call
```

Retry wraps Timeout (retry the whole timed call).
CircuitBreaker wraps Retry (don't retry if circuit is open).

---

## Spring Boot Integration

```xml
<dependency>
  <groupId>io.github.resilience4j</groupId>
  <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
```

```java
@CircuitBreaker(name = "payment", fallbackMethod = "payFallback")
@Retry(name = "payment")
public Order pay(Request req) { ... }

public Order payFallback(Request req, Exception ex) { return cachedOrder; }
```

```yaml
resilience4j.circuitbreaker.instances.payment:
  failureRateThreshold: 50
  waitDurationInOpenState: 10s
  slidingWindowSize: 10
```

---

## Interview Points

- Circuit breaker protects the *caller* from waiting + protects the *downstream* from load.
- Retry without idempotency = duplicate orders/payments. Always check idempotency first.
- Bulkhead = thread isolation. Circuit breaker = failure rate tracking. Both are needed.
- Timeout is the most important single pattern — everything else builds on it.
- Rate limiter protects downstream. Throttling (same concept) protects your own service from overload.
