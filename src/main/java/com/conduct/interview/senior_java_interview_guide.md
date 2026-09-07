# Senior Java Developer — Interview Guide

---

## 1. Java Memory Model

**Stack** — per thread, LIFO, holds local variables + references. Freed when method exits.
**Heap** — shared, holds all objects. Managed by GC. OutOfMemoryError when full.
**Metaspace** — off-heap, holds class metadata (bytecode, annotations).

**Generations**: Young (Eden + 2 Survivors) → Old (tenured). Minor GC clears Young (fast). Full GC clears all (slow, causes pause).

**Static fields**: live on the Heap inside the Class object. Class metadata is in Metaspace.

**Escape Analysis**: if JIT proves object doesn't escape the method, allocates on Stack (no GC).
**TLAB**: per-thread buffer in Eden — threads allocate objects without synchronization.

Key interview points:
- StackOverflowError = stack too deep (infinite recursion). OutOfMemoryError = heap full.
- GC roots: static fields, local variables in stack frames, JNI references.
- `-Xms` / `-Xmx` set initial/max heap. `-Xss` sets stack size per thread.

---

## 2. Java Concurrency

**volatile** — visibility guarantee (no CPU cache), no atomicity. `i++` on volatile is still a race.
**synchronized** — mutual exclusion + happens-before. Reentrant. Coarse-grained.
**ReentrantLock** — explicit lock. `tryLock(timeout)`, interruptible, fair mode.
**ReentrantReadWriteLock** — multiple readers OR one writer. Good for read-heavy caches.

**Atomic classes** — CAS-based, lock-free. `AtomicInteger`, `AtomicReference`, `LongAdder` (less contention than AtomicLong).

**CompletableFuture**:
```java
CompletableFuture.supplyAsync(() -> fetchUser(id))
    .thenApply(user -> enrich(user))          // transform, same thread
    .thenCompose(u -> fetchOrders(u.id()))    // flatMap (another future)
    .exceptionally(ex -> fallback());
```

**Virtual Threads (Java 21)**: JVM-managed, millions possible, cheap (few KB). IO blocks virtual thread, not OS thread.
Use `Executors.newVirtualThreadPerTaskExecutor()`. `synchronized` pins to OS thread — use `ReentrantLock` instead.

**ConcurrentHashMap**: lock-striped, thread-safe. Use `computeIfAbsent` / `merge` for atomic operations.

**Deadlock**: A holds lock-1 waits for lock-2, B holds lock-2 waits for lock-1. Fix: always acquire in same order.

Key interview points:
- `volatile` ≠ atomic. `AtomicInteger.incrementAndGet()` ≠ `synchronized`.
- `CompletableFuture` default executor is `ForkJoinPool.commonPool()` — never block on it.
- Virtual threads are for IO-bound, not CPU-bound.

---

## 3. OOP

**Abstraction** — hide implementation, expose interface.
**Encapsulation** — private state, controlled access via public methods.
**Inheritance** — `extends` (class), `implements` (interface). Only public/protected members inherited.
**Polymorphism** — static (overloading, compile-time) / dynamic (overriding, runtime dispatch).
**Composition** — has-a, strong lifecycle dependency. **Aggregation** — has-a, independent lifecycle.

OOP vs FP: OOP models entities + state; FP models transformations + immutability. Java supports both.

---

## 4. SOLID

**S** — Single Responsibility: one reason to change. Class does one thing.
**O** — Open/Closed: extend behavior without modifying existing code (strategy pattern, inheritance).
**L** — Liskov Substitution: subtype must be usable wherever the base type is. Don't narrow postconditions, don't widen exceptions.
**I** — Interface Segregation: many small interfaces > one fat interface. Clients only depend on what they use.
**D** — Dependency Inversion: depend on abstractions, not concretions. Inject dependencies (Spring DI).

---

## 5. Design Patterns (Most Asked)

**Creational:**
- **Singleton** — one instance, lazy/eager, thread-safe via `synchronized` or enum.
- **Factory Method** — subclass decides which object to create.
- **Builder** — step-by-step construction of complex objects.

**Structural:**
- **Proxy** — wraps an object to add behavior (logging, security, caching). Spring AOP uses JDK proxy / CGLIB.
- **Decorator** — adds behavior dynamically by wrapping. Differs from proxy: decorator adds features, proxy controls access.
- **Adapter** — makes incompatible interfaces work together.
- **Facade** — simplified interface to a complex subsystem.

**Behavioral:**
- **Strategy** — interchangeable algorithms behind an interface.
- **Observer** — event listeners. Publisher notifies all subscribers.
- **Template Method** — skeleton in base class, steps overridden in subclass.
- **Command** — encapsulate a request as an object (undo/queue/log).

---

## 6. Spring Framework

**IoC / DI**: Spring creates and wires beans. `@Autowired` → field/constructor/setter injection. Constructor injection preferred.
**Bean lifecycle**: `@PostConstruct` → use → `@PreDestroy`. `BeanPostProcessor` wraps beans (AOP, transactions).
**Scopes**: `singleton` (default, one per context) / `prototype` (new each time) / `request` / `session`.
**AOP**: `@Aspect` + `@Around`/`@Before`/`@After`. Spring uses JDK Proxy (interface) or CGLIB (class). Self-invocation bypasses proxy.
**Transactions**: `@Transactional` = AOP proxy → `TransactionInterceptor`. Only unchecked exceptions roll back by default. Self-invocation = no transaction.
**Propagation**: `REQUIRED` (join), `REQUIRES_NEW` (suspend+new), `NESTED` (savepoint).

Spring Boot auto-configuration: `@EnableAutoConfiguration` → scans `spring.factories` / `AutoConfiguration.imports` → conditionally registers beans.

---

## 7. Transactions & ACID

**Atomicity** — all or nothing. Enforced by undo log.
**Consistency** — constraints always hold before and after.
**Isolation** — concurrent transactions don't interfere.
**Durability** — committed data survives crashes (WAL flushed to disk before commit returns).

| Isolation Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|---|---|---|---|
| READ UNCOMMITTED | possible | possible | possible |
| READ COMMITTED | prevented | possible | possible |
| REPEATABLE READ | prevented | prevented | possible |
| SERIALIZABLE | prevented | prevented | prevented |

Default: READ COMMITTED (PostgreSQL, Oracle), REPEATABLE READ (MySQL InnoDB).

Interview points: `@Transactional` on `private` = does nothing (proxy can't intercept). Checked exceptions don't rollback by default.

---

## 8. REST & HTTP

REST constraints: stateless, client-server, cacheable, uniform interface, layered, code-on-demand.

**HTTP methods**: GET (safe+idempotent), PUT (idempotent), DELETE (idempotent), POST (neither), PATCH (neither but should be).

**Status codes**: 200 OK, 201 Created, 204 No Content, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict, 422 Unprocessable, 429 Too Many Requests, 500 Internal, 503 Unavailable.

**Versioning**: URL (`/api/v2/orders`), header (`Accept: application/vnd.api+json;version=2`), query param.

**Pagination**: cursor-based (stable, scalable) > offset-based (drifts on inserts).

**Idempotency key**: unique UUID in header for POST operations. Server deduplicates on retry.

---

## 9. Databases

**SQL Indexing**: B-tree (default), range-friendly. Hash (exact match only). Full-text. Composite index — leftmost prefix rule.
**N+1 problem**: fetching 1 parent → N queries for children. Fix: JOIN fetch / `@EntityGraph` / batch size.
**EXPLAIN ANALYZE**: shows actual execution plan, seq scan vs index scan, cost, rows.
**Transactions isolation in Postgres**: default READ COMMITTED. Serializable for strict consistency.

**Hibernate / JPA**: `@Entity`, `@OneToMany(fetch=LAZY)` (default). EAGER = N+1 trap. Use `@BatchSize` or `JOIN FETCH`.
**First-level cache**: per session. Second-level cache: shared (EHCache, Redis).

**NoSQL**: Document (MongoDB, flexible schema), Key-Value (Redis, DynamoDB), Column (Cassandra, time-series), Graph (Neo4j).
**MongoDB**: rich queries, ACID since 4.0. **Cassandra**: write-optimized, wide rows, tunable consistency, no joins.

---

## 10. Message Brokers

**Kafka** — distributed event log. Persistent, replayable. Multiple independent consumer groups. At-most/at-least/exactly-once. DLQ is manual.
**RabbitMQ** — smart broker. Exchange types: direct (exact key), topic (wildcard), fanout (broadcast). Native DLX. Fair dispatch via `basicQos(1)`.
**Redis Pub/Sub** — at-most-once, no persistence. **Redis Streams** — persistent, consumer groups, XACK-based at-least-once. DLQ via PEL+XAUTOCLAIM.

Delivery strategies:
- At-most-once: commit/ack before processing. Fast, may lose.
- At-least-once: commit/ack after processing. May duplicate. Consumer must be idempotent.
- Exactly-once: Kafka transactions only (Kafka→Kafka). ~2-3× overhead.

DLQ: Kafka = manual produce to dlq topic + commitSync. RabbitMQ = `basicNack(requeue=false)` → DLX auto-routes. Redis = XAUTOCLAIM stuck PEL → XADD to dlq-stream.

---

## 11. Event-Driven Architecture

**EDA patterns**: Event Notification (lightweight ping), Event-Carried State Transfer (fat event, consumers autonomous), Event Sourcing (events are source of truth).

**Choreography** vs **Orchestration**: choreography = decentralized (each service reacts), orchestration = central coordinator (Saga orchestrator).

**Outbox Pattern** (dual-write fix): write event to outbox table in same DB transaction as state change → separate publisher reads and publishes.

**Saga**: sequence of local transactions with compensating transactions on failure. Choreography (events) or orchestration (orchestrator sends commands).

**Spring Events**: `@EventListener` (sync, same thread), `@Async @EventListener` (background), `@TransactionalEventListener(AFTER_COMMIT)` — fires only if TX committed. Solves dual-write within one JVM.

---

## 12. Concurrency (Advanced — Java Concurrency)

See Section 2 for details. Key patterns:
- Thread-safe singleton: `enum` or `volatile` double-checked locking.
- Read-heavy cache: `ReentrantReadWriteLock` or `ConcurrentHashMap`.
- Fan-out async work: `CompletableFuture.allOf(...)`.
- IO-bound at scale: Virtual Threads (Java 21).

---

## 13. Resilience Patterns

**Circuit Breaker**: CLOSED → (failure threshold) → OPEN (fast-fail) → (wait) → HALF-OPEN → (trial) → CLOSED.
**Retry**: with exponential backoff + jitter. Never retry non-idempotent writes without idempotency key.
**Bulkhead**: separate thread pools per dependency. One slow service can't exhaust all threads.
**Rate Limiter**: token bucket (bursty allowed) — protects downstream.
**Timeout**: always set. Most important single pattern.

Correct composition order: `RateLimiter → CircuitBreaker → Retry → Timeout → Bulkhead → call`

Spring Boot: `resilience4j-spring-boot3` + `@CircuitBreaker(name="..", fallbackMethod="..)")`.

---

## 14. System Design

**CAP**: C=Consistency, A=Availability, P=Partition tolerance. P is non-negotiable. Choose CP (HBase, etcd) or AP (Cassandra, DynamoDB).

**Consistency models**: Eventual → Read-your-writes → Monotonic reads → Causal → Strong (linearizable).

**Caching**: Cache-aside (lazy, miss→load), Write-through (sync), Write-behind (async, risk loss), Read-through.
**Eviction**: LRU (most common), LFU, TTL.

**Scaling**: Vertical (limit) vs Horizontal (stateless required). Read replicas (reads) → Sharding (last resort, avoid cross-shard joins).

**Load balancing**: Round-robin, Least connections, IP hash (sticky), Consistent hashing (cache shards).

**Rate limiting**: Fixed window (burst at boundary), Sliding window (accurate), Token bucket (smooth bursts).

---

## 15. Microservices

**Core principles**: Single business capability per service, own DB (database-per-service), independent deploy.

**Why database-per-service**: loose coupling (no shared schema), polyglot persistence, independent scaling, team ownership.

**Service communication**: sync (HTTP/REST, gRPC) vs async (Kafka, RabbitMQ). Prefer async for cross-service events to avoid temporal coupling.

**API Gateway**: single entry, handles auth, rate limiting, routing, SSL, transforms. Backends-for-Frontends (BFF) pattern for mobile vs web.

**Service Discovery**: Kubernetes DNS / Eureka. Load balancer uses health check to route only to healthy instances.

**Distributed transactions**: avoid 2PC (blocks). Use Saga + compensating transactions.

---

## 16. Observability

**Three pillars**: Logs (what happened), Metrics (how many/fast), Traces (where is it slow).

**Logs**: structured (JSON), correlation ID in every line via MDC, levels: ERROR/WARN/INFO/DEBUG.
**Metrics**: 4 Golden Signals — Latency (p99), Traffic (req/s), Errors (5xx rate), Saturation (CPU/memory). Spring Actuator + Micrometer + Prometheus + Grafana.
**Traces**: OpenTelemetry standard. TraceId propagated across services. Span = one service hop. Sample 10% in prod.

**Health checks**: `/actuator/health/liveness` (restart if dead) ≠ `/actuator/health/readiness` (route traffic). Never put DB in liveness — one DB blip restarts all pods.

**Alerting on symptoms** (latency, error rate), not causes (CPU %).

---

## 17. CI/CD

**CI pipeline**: push → compile → test → static analysis → build image → push registry.
**CD**: Delivery (manual deploy) vs Deployment (auto deploy). Most teams: auto to staging, manual to prod.

**Deployment strategies**:
- **Rolling**: replace instances one at a time. Zero downtime. Old+new code run simultaneously → DB must be backward-compatible.
- **Blue/Green**: switch traffic instantly. Easy rollback. Double infrastructure.
- **Canary**: 5% traffic to new version, monitor, gradually increase. Safest.
- **Feature flags**: deploy disabled, enable for % of users without redeployment.

**DB migrations in CI/CD**: Flyway/Liquibase. Always backward-compatible with previous version. Add nullable columns. Drop only after old code is gone (two deployments later).

**Docker multi-stage build**: build in JDK image, copy artifact to JRE image → small prod image. Tag with git SHA, not `latest`.

---

## 18. Auth & Security

**OAuth2**: Resource Owner → Authorization Server → Access Token → Resource Server.
Flows: Authorization Code (web apps, secure), Client Credentials (service-to-service), Device Flow (TV/CLI).
**JWT**: Header.Payload.Signature. Stateless — verify signature without DB. Never store sensitive data in payload (it's base64, not encrypted).

**OWASP Top 10 (most critical)**:
- Injection (SQL, NoSQL, LDAP) — use parameterized queries, never string concat.
- Broken Authentication — use proven libraries (Spring Security), short token TTL, refresh tokens.
- Insecure Direct Object Reference (IDOR) — validate ownership, not just authentication.
- Security Misconfiguration — disable default creds, hide stack traces, use HTTPS.
- XSS — encode output, Content-Security-Policy header.
- CSRF — SameSite cookie, CSRF token.

Spring Security: filter chain, `SecurityFilterChain`, `UserDetailsService`, JWT filter, method security `@PreAuthorize`.

---

## 19. Reactive Programming

**Reactive Streams spec**: Publisher → Subscriber with backpressure. Project Reactor implements it: `Mono<T>` (0-1), `Flux<T>` (0-N).

`map` (sync, 1:1) vs `flatMap` (async, 1:N, unordered) vs `concatMap` (ordered flatMap).

**Backpressure**: subscriber signals demand upstream. `onBackpressureDrop`, `onBackpressureBuffer`, `limitRate`.

**Schedulers**: `Schedulers.boundedElastic()` for IO-blocking. `Schedulers.parallel()` for CPU. Don't block on `parallel()`.

**Cold** (lazy, new stream per subscriber) vs **Hot** (shared, `Sinks`, `share()`, `publish()`).

Key: never call `.block()` on a thread that Spring WebFlux needs (will deadlock). Use `subscribeOn(boundedElastic)` to offload blocking IO.

---

## 20. AWS (Key Services)

**EC2**: VMs. **Lambda**: serverless functions, triggered by events, pay-per-invocation. Cold start mitigation: provisioned concurrency.
**S3**: object storage, 11-9s durability, pre-signed URLs for secure direct upload/download.
**RDS**: managed relational DB (PostgreSQL, MySQL). Multi-AZ = HA. Read replicas = scale reads.
**DynamoDB**: NoSQL key-value, single-digit ms at any scale, on-demand capacity.
**SQS**: message queue (at-least-once, max 256KB). **SNS**: pub/sub fan-out. SQS+SNS = fan-out queue pattern.
**ElastiCache**: managed Redis/Memcached.
**API Gateway**: HTTP/WebSocket APIs with throttling, auth, caching.
**ECS/EKS**: container orchestration. ECS=AWS-native, EKS=Kubernetes.
**CloudWatch**: metrics, logs, alarms. X-Ray: distributed tracing.
**IAM**: roles + policies. Least privilege. Use instance roles, not hardcoded credentials.
**VPC**: isolated network. Public subnet (internet-facing), private subnet (DBs, internal). Security Groups = stateful firewall.

---

## 21. gRPC

RPC framework using **HTTP/2** + **Protocol Buffers** (binary, ~3-10× smaller than JSON). Strict typed contract via `.proto` files → code-generated client + server stubs.

**vs REST**:
- gRPC: binary, fast, streaming, strict contract, poor browser support → internal service-to-service.
- REST: text, universal browser support, no code gen required → public APIs.

**4 modes**: Unary (req/resp), Server Streaming (server pushes N), Client Streaming (client sends N), Bi-directional (both stream).

**HTTP/2 benefits**: multiplexing (many calls, one TCP connection), header compression, binary framing — no head-of-line blocking.

```java
@GrpcService
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {
    @Override
    public void getOrder(GetOrderRequest req, StreamObserver<Order> resp) {
        resp.onNext(orderService.find(req.getOrderId()));
        resp.onCompleted();
    }
}
```

Error model: rich status codes (`NOT_FOUND`, `DEADLINE_EXCEEDED`, `UNAVAILABLE`) vs HTTP status codes.
Always set deadlines: `stub.withDeadlineAfter(2, TimeUnit.SECONDS).getOrder(req)`.
Schema evolution: add fields freely (backward compat), never reuse field numbers.

---

## 22. Kubernetes

Container orchestration. You declare desired state → K8s continuously reconciles actual state.

**Core objects**:
- **Pod** — one or more containers, shared network. Ephemeral — always use a Deployment.
- **Deployment** — manages replicas, rolling updates, rollback.
- **Service** — stable DNS + IP for a pod set. `ClusterIP` (internal), `LoadBalancer` (external).
- **Ingress** — Layer 7 HTTP routing by host/path to Services.
- **ConfigMap / Secret** — externalise config from image. Secrets = base64 only (use Vault in prod).
- **HPA** — auto-scale replicas on CPU/memory/custom metrics.

**Requests vs Limits**: Requests = guaranteed (scheduler uses). Limits = max (CPU throttled, memory → OOMKilled). Always set both.

**Probes**:
- `livenessProbe` → restart if unhealthy. Never put DB here — one DB blip kills all pods.
- `readinessProbe` → remove from Service endpoints if not ready (booting, overloaded).
- `startupProbe` → delay liveness for slow-starting apps.

**Rolling update**: `maxSurge: 1, maxUnavailable: 0` = zero-downtime. Requires backward-compatible DB migrations.

Key commands: `kubectl describe pod`, `kubectl logs -f --previous`, `kubectl rollout undo`, `kubectl top pods`.

---

## 23. Performance Profiling

**Rule**: measure first, optimize the bottleneck. Top bottlenecks in order: DB queries (N+1, missing index) → external calls (sequential, no timeout) → GC pressure → thread contention → CPU.

**Heap dump** (OutOfMemoryError, memory leak):
```bash
jcmd <pid> VM.heap_dump /tmp/heap.hprof
```
Analyze with Eclipse MAT: dominator tree, leak suspects, retained heap.
Common leaks: static collections without eviction, unregistered listeners, ThreadLocal in thread pools, Hibernate L1 cache bloat.

**Thread dump** (hang, deadlock, high CPU):
```bash
jcmd <pid> Thread.print
```
Look for: `BLOCKED` threads on same lock (contention), deadlock section at bottom.

**Async Profiler** — production-safe CPU/allocation flamegraph (1-3% overhead):
```bash
./asprof -d 30 -f flamegraph.html <pid>   # CPU
./asprof -e alloc -d 30 -f alloc.html <pid>  # allocation
```
Flamegraph: wide = hot. Optimize the wide flat bars near the top.

**JFR** (Java Flight Recorder) — always-on in prod, ~1% overhead, free since Java 11:
```bash
jcmd <pid> JFR.start duration=60s filename=rec.jfr
```
Analyze with JDK Mission Control: GC pauses, lock contention, hot allocations.

**HikariCP pool exhaustion** — common prod issue. Set `leak-detection-threshold: 2000`. Alert on `hikaricp_pending_threads > 0`.

**Parallel external calls** — replace sequential CompletableFuture chains with `allOf`:
```java
// 3 × 300ms sequential = 900ms → parallel = 300ms
CompletableFuture.allOf(
    supplyAsync(() -> userService.get(id)),
    supplyAsync(() -> orderService.get(id))
).join();
```

GC choice: G1GC (default, good balance). ZGC (Java 15+, <1ms pauses, large heaps). Shenandoah (RedHat, similar).

---

## Interview Advice

- Always explain trade-offs, not just the solution.
- For any pattern: state when you'd use it AND when you wouldn't.
- "It depends" is a good start if followed by specific criteria.
- Mention observability (logs/metrics/traces) unprompted — shows production experience.
- Bring up failure modes: what happens if this service crashes? if the DB is slow? if the network partitions?
- For system design: start with requirements, then data model, then API, then scaling concerns — don't jump to tech choices.
