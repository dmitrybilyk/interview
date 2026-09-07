# Performance & Profiling

## What to Measure First

**Don't optimize blindly.** Measure first, then fix the bottleneck.

Common bottlenecks in order of frequency:
1. **DB queries** — N+1, missing index, full table scan
2. **External calls** — no timeout, no connection pool, sequential instead of parallel
3. **GC pressure** — too many short-lived objects, large heap causing long Full GC pauses
4. **Thread contention** — lock congestion, thread pool exhaustion
5. **CPU** — tight loops, inefficient algorithms (rare in typical Spring apps)

---

## JVM Flags for Diagnostics

```bash
# GC logging (Java 9+)
-Xlog:gc*:file=gc.log:time,uptime:filecount=5,filesize=20m

# Heap size
-Xms512m -Xmx2g

# Heap dump on OOM (essential in prod)
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof

# GC algorithm choices
-XX:+UseG1GC          # default Java 9+ — good balance
-XX:+UseZGC           # Java 15+ — ultra-low pause (<1ms), good for large heaps
-XX:+UseShenandoahGC  # low pause, Red Hat

# Print compilation
-XX:+PrintCompilation
```

---

## Heap Dump Analysis

When: `OutOfMemoryError`, memory leak suspicion, unexpectedly high heap usage.

```bash
# Trigger heap dump on running process
jmap -dump:format=b,file=heap.hprof <pid>

# Or use jcmd (preferred)
jcmd <pid> VM.heap_dump /tmp/heap.hprof
```

**Tools**: VisualVM (free), Eclipse MAT (Memory Analyzer Tool — best for large dumps), IntelliJ heap analyzer.

**What to look for in MAT**:
- **Dominator tree**: which objects retain the most memory.
- **Leak suspects report**: auto-detects collections growing without bound.
- **Retained heap**: total memory freed if this object is GC'd.

Common leak patterns:
- `static` collection accumulating items never removed (e.g., static cache without eviction).
- Listeners/callbacks registered but never unregistered.
- ThreadLocal not cleaned up in thread pool threads.
- Hibernate session holding too many entities in first-level cache.

---

## Thread Dump Analysis

When: application hangs, high CPU with no throughput, suspected deadlock.

```bash
jstack <pid> > thread.dump
# or
jcmd <pid> Thread.print > thread.dump
```

**What to look for**:
- **BLOCKED** threads all waiting on the same lock → lock contention.
- **WAITING** on `Object.wait()` → possibly stuck waiting for work.
- **Deadlock** section at the bottom of jstack output — explicit.
- All threads in `TIMED_WAITING` in pool but nothing executing → thread starvation.

**VisualVM / IntelliJ**: visual thread timeline, easier to spot patterns.

---

## Async Profiler (CPU + Allocation Profiling)

Production-safe, low overhead (~1-3%). Samples stack traces at configurable interval.
No instrumentation — uses `AsyncGetCallTrace` + perf events.

```bash
# Download: https://github.com/async-profiler/async-profiler

# CPU profiling for 30s → flamegraph
./asprof -d 30 -f flamegraph.html <pid>

# Allocation profiling
./asprof -e alloc -d 30 -f alloc.html <pid>

# Wall-clock (includes blocked time — good for IO diagnosis)
./asprof -e wall -d 30 -f wall.html <pid>
```

**Reading a flamegraph**: x-axis = proportion of time (wider = more time). y-axis = call stack.
Flat wide bars at the top = hot methods (optimize these).

---

## JFR — Java Flight Recorder

Built into JVM (Java 11+, free). Low overhead (~1%). Records events: GC, allocations, exceptions, IO, locks.

```bash
# Start recording
jcmd <pid> JFR.start duration=60s filename=recording.jfr

# Dump now
jcmd <pid> JFR.dump filename=recording.jfr
```

**JDK Mission Control (JMC)**: GUI to analyze `.jfr` files. Shows GC pauses, hot methods, lock contention, memory allocation by class.

---

## Connection Pool Tuning (HikariCP)

DB connection pool exhaustion is a common production issue.

```yaml
spring.datasource.hikari:
  maximum-pool-size: 10       # default — often too low for high concurrency
  minimum-idle: 5
  connection-timeout: 3000    # fail fast if no connection available (ms)
  idle-timeout: 600000
  max-lifetime: 1800000
  leak-detection-threshold: 2000  # log if connection held > 2s (finds leaks)
```

**Sizing formula** (PostgreSQL team): `connections = (core_count * 2) + effective_spindle_count`.
More connections ≠ faster. Context switching overhead increases. Test with your actual load.

**Monitoring**: `hikaricp_pending_threads` metric in Micrometer. Alert if > 0 sustained.

---

## Common Performance Patterns

**N+1 query**:
```java
// BAD: 1 query for orders + N queries for each order's items
orders.forEach(o -> o.getItems().size());  // LAZY load triggers N queries

// GOOD: one JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.userId = :uid")
```

**Sequential external calls → parallel**:
```java
// BAD: 3 calls sequential = 300ms+300ms+300ms = 900ms
User u = userService.get(id);
Orders o = orderService.get(id);
Prefs p = prefService.get(id);

// GOOD: parallel = max(300ms, 300ms, 300ms) = 300ms
CompletableFuture<User>   fu = CompletableFuture.supplyAsync(() -> userService.get(id));
CompletableFuture<Orders> fo = CompletableFuture.supplyAsync(() -> orderService.get(id));
CompletableFuture<Prefs>  fp = CompletableFuture.supplyAsync(() -> prefService.get(id));
CompletableFuture.allOf(fu, fo, fp).join();
```

---

## Interview Points

- "Profile first, optimize second." Knowing *where* the bottleneck is matters more than premature optimization.
- Heap dump + MAT for memory issues. Thread dump + jstack for hangs. Async profiler for CPU/allocation.
- G1GC is the default and fine for most apps. ZGC for latency-sensitive apps with large heaps (>4GB).
- HikariCP pool exhaustion is a top-3 production issue. `leak-detection-threshold` is underused.
- JFR is free in production — always have it enabled with continuous recording (ring buffer, ~1% overhead).
- `ThreadLocal` in thread pool = memory leak unless explicitly removed in `finally`.
