# Java Concurrency

## Core Problem
Multiple threads share memory. Without coordination: race conditions, stale reads, deadlocks.

---

## Memory Model — Happens-Before

The JMM guarantees visibility between threads only at explicit synchronization points.
Without them, a write on thread A may never be seen by thread B.

```java
// BROKEN — thread B may loop forever (CPU cache, reordering)
boolean running = true;
// thread B: while (running) {}
// thread A: running = false;   ← B may never see this

// FIXED
volatile boolean running = true;
```

**`volatile`**: guarantees visibility (no caching) + prevents instruction reordering.
Does NOT make compound operations atomic. `i++` on a volatile is still a race condition.

---

## synchronized

```java
synchronized (lock) { /* only one thread at a time */ }
```

- Establishes happens-before: release of lock by A → acquisition by B sees all of A's writes.
- Intrinsic lock is reentrant (same thread can re-enter).
- Downside: blunt — one thread at a time regardless of read vs write.

---

## java.util.concurrent Locks

```java
ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
lock.readLock().lock();   // multiple readers OK simultaneously
lock.writeLock().lock();  // exclusive
```

**ReentrantLock** over `synchronized` when: try-lock with timeout, interruptible lock, fairness.

---

## Atomic Classes

Lock-free, CAS-based (Compare-And-Swap). Faster than synchronized for single variables.

```java
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();        // atomic i++
counter.compareAndSet(5, 10);     // set to 10 only if current == 5
```

`AtomicReference`, `AtomicLong`, `LongAdder` (better than AtomicLong under high contention).

---

## Thread Pools — ExecutorService

Never create raw `new Thread()` in production. Use pools — thread creation is expensive.

```java
ExecutorService pool = Executors.newFixedThreadPool(10);
pool.submit(() -> doWork());
pool.shutdown();
```

| Pool type | Use case |
|---|---|
| `newFixedThreadPool(n)` | CPU-bound tasks, n = CPU cores |
| `newCachedThreadPool()` | Many short-lived IO tasks (grows unbounded!) |
| `newScheduledThreadPool(n)` | Periodic / delayed tasks |
| `ForkJoinPool.commonPool()` | Parallel streams, CompletableFuture |

**Always shutdown your pool** or it keeps the JVM alive.

---

## CompletableFuture

Non-blocking async composition. Avoids callback hell.

```java
CompletableFuture.supplyAsync(() -> fetchUser(id))          // async, pool thread
    .thenApply(user -> enrich(user))                        // transform result
    .thenCompose(user -> fetchOrders(user.id()))            // flat-map (another future)
    .thenCombine(fetchInventory(), (orders, inv) -> merge(orders, inv))  // zip two futures
    .exceptionally(ex -> fallback())                        // error handling
    .thenAccept(result -> respond(result));
```

- `thenApply` — sync transform (same thread).
- `thenApplyAsync` — transform on pool thread.
- `thenCompose` — chain dependent futures (flatMap).
- `allOf` / `anyOf` — wait for all / first of N futures.

---

## Virtual Threads (Java 21 — Project Loom)

Platform threads map 1:1 to OS threads — expensive (1MB stack, ~1ms creation).
Virtual threads are JVM-managed, cheap (few KB, millions possible).

```java
// Old: pool of N threads blocking on IO → N threads max concurrency
ExecutorService pool = Executors.newFixedThreadPool(200);

// New: unlimited virtual threads, blocking IO doesn't block OS thread
ExecutorService vt = Executors.newVirtualThreadPerTaskExecutor();
vt.submit(() -> {
    var result = httpClient.send(request, ...);  // blocks virtual thread, not OS thread
});
```

**When to use**: IO-bound work (HTTP calls, DB queries). NOT for CPU-bound (no benefit).
**Key insight**: `synchronized` pins virtual thread to carrier OS thread — use `ReentrantLock` instead.

---

## ConcurrentHashMap

Thread-safe, lock-striped (16 segments by default → low contention). Not fully locked.

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.computeIfAbsent("key", k -> expensiveLoad(k));  // atomic
map.merge("key", 1, Integer::sum);                  // atomic increment
```

`putIfAbsent` / `computeIfAbsent` / `merge` are all atomic. Plain `get`/`put` are also thread-safe.

---

## Common Problems

**Race condition** — two threads read-modify-write same data without synchronization.
Fix: `synchronized`, `AtomicInteger`, or lock.

**Deadlock** — A holds lock-1 waits for lock-2, B holds lock-2 waits for lock-1.
Fix: always acquire locks in the same order. Use `tryLock` with timeout.

**Livelock** — threads keep reacting to each other, no progress. Rare, fix with backoff.

**Starvation** — low-priority thread never gets CPU. Fix: fair locks or equal priority.

---

## Interview Points

- `volatile` = visibility only, NOT atomicity.
- `synchronized` = mutual exclusion + visibility (establishes happens-before).
- `AtomicInteger.incrementAndGet()` is lock-free (CAS loop), not `synchronized`.
- Virtual threads don't improve CPU-bound work — only IO-bound.
- `CompletableFuture` default executor is `ForkJoinPool.commonPool()` — don't block on it.
- `ConcurrentHashMap` is NOT safe for check-then-act (`if !contains → put`) — use `computeIfAbsent`.
