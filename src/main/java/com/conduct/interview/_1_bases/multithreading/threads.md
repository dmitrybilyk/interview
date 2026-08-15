# Threads

A thread is a unit of execution within a process. In Java, threads are created by the JVM and
mapped to native OS threads.

Threads are lightweight compared to processes because they share the same heap memory, but each
thread has its own stack. That shared memory is what makes threads useful (easy communication
between them) and also what causes all the classic problems: race conditions, visibility issues,
deadlocks (see `common_issues/`).

### Concurrency vs Parallelism
- **Concurrency** - managing multiple tasks at once (can be on a single core, interleaved)
- **Parallelism** - actually executing tasks at the same time (needs multiple cores)

### Why use threads
- better CPU utilization (parallel execution)
- non-blocking behavior (e.g. handling I/O without freezing everything else)
- responsiveness (UI, APIs)

### Latency vs Throughput
Two different reasons to add threads - knowing which one you're optimizing for is a common
interview question:

- **Latency** = how long *one* task takes to finish. Reduced by splitting that one task across
  threads so the pieces run in parallel (e.g. summing a huge array with 4 threads instead of 1 -
  see `udemy/latency/LatencyOptimizationWithThreads.java`). Limited by how much of the task can
  actually be parallelized (Amdahl's law) - some work is inherently sequential.
- **Throughput** = how many tasks/requests get completed *in total*, per unit of time. Improved
  by running many independent tasks concurrently, e.g. a thread pool serving HTTP requests (see
  `udemy/throughput/ThroughputHttpServer.java`) - each individual request isn't faster, but more
  of them finish per second.

More threads doesn't always help either goal: past a point, context-switching and lock
contention make things worse, not better - for CPU-bound work, thread count should track CPU
cores (see `types_of_work/cpu_heavy_work.md`).

### The basics, before anything else
- `thread_creation` - the 3 ways to run code on a thread, and why Runnable/Callable is preferred
- `thread_lifecycle` - the states a thread moves through (NEW, RUNNABLE, BLOCKED, ...)
- `daemon_threads` - threads that don't keep the JVM alive

---

# All multithreading notes (combined)

The rest of this file is every other `.md` in this `multithreading` package, concatenated for
quick full-text search/skim. Each topic's real home is its own folder - this is just a single
place to Ctrl+F across everything.

---

## `common_issues/_10_concurrency_bugs_data_structures/concurrency_bugs_data_structures.md`

Most collections (`HashMap`, `ArrayList`, `HashSet`...) aren't thread-safe. Two threads writing
at once can corrupt internal state and silently lose entries - no exception, just a wrong result.

Fix: use a thread-safe collection, e.g. `ConcurrentHashMap` instead of `HashMap`.


---

## `common_issues/_2_deadlock/deadlock/deadlock.md`

Deadlock is the situation when 2 threads are waiting for the lock
to be open. One thread occupies lock on objA and tries to get the lock
on objB and another thread enters lock of objB and tries to get lock on
objA. 
Can be fixed with putting the locking into the same order.

---

## `common_issues/_7_blocking_operations/blocking_operations.md`

# Terminal 1
telnet localhost 8088

# Terminal 2
telnet localhost 8088

---

## `common_issues/_8_thread_leaks/thread_leaks.md`

Thread leak: a thread is created but never finishes and never gets cleaned up, so it keeps
eating memory until the app crashes (`OutOfMemoryError: unable to create new native thread`).

Fix: don't spawn raw `new Thread(...)` per request/task - use an `ExecutorService` instead.
But the `ExecutorService` itself leaks the same way if you never call `shutdown()` on it - its
threads stay alive forever too. Also make sure blocking calls (network, DB, queue) have timeouts,
so a thread can't get stuck waiting forever.


---

## `common_issues/issues.md`

# Common Java Multithreading Issues

### 1. Race Conditions
- **Issue**: Threads access shared resources without synchronization.
- **Solution**: Use `synchronized`, locks, or atomic classes.

### 2. Deadlocks
- **Issue**: Threads wait on each other, causing a stuck state.
- **Solution**: Avoid nested locks or use `tryLock()`.

### 3. Livelock
- **Issue**: Threads respond to each other without making progress.
- **Solution**: Use retry strategies with increasing wait times.

### 4. Thread Starvation
- **Issue**: High-priority threads block lower-priority ones.
- **Solution**: Use fair locks and avoid priority-based threading.

### 5. Context Switching Overhead
- **Issue**: Frequent thread switching degrades performance.
- **Solution**: Minimize thread count and use thread pools.

### 6. Memory Consistency Errors
- **Issue**: Threads see inconsistent shared variable values.
- **Solution**: Use `volatile` or synchronized access.
Volatile solves visibility issue when single thread writes. If multiple thread write then
it's atomicity problem which needs to be solved by synchronized keyword or locking or
atomic types.

### 7. Blocking Operations
- **Issue**: Threads blocked by I/O hold resources too long.
- **Solution**: Use non-blocking I/O or async mechanisms.

### 8. Thread Leaks
- **Issue**: Threads created but not terminated cause resource leaks.
- **Solution**: Shut down thread pools and manage lifecycles.
We should never create threads with new Thread. Instead we should use
ThreadPools which guarantee proper lifecycle, queueing etc.
Just we should always shutdown executors to avoid leaking as well.

### 9. Improper Thread Pool Use
- **Issue**: Too few or too many threads in a pool impacts performance.
- **Solution**: Choose suitable thread pool sizes.
In case of CPU operations (heavy calculations etc.) thread pool size could be equal to
number CPU cores to gradually split load between cores. In case of IO operations (http calls, db call etc)
thread pools size should be calculated with taking into account waiting time, so would be 10 times bigger

### 10. Concurrency Bugs in Data Structures
- **Issue**: Concurrent data structure use causes issues.
- **Solution**: Use thread-safe collections like `ConcurrentHashMap`.

### 11. Fork/Join Pool Misuse
- **Issue**: Task imbalance leads to poor performance.
- **Solution**: Divide tasks evenly and follow Fork/Join patterns.
  Misusing the Fork/Join pool occurs when you execute blocking I/O operations or choose an inefficient task threshold, 
  which leads to thread starvation and excessive context-switching overhead.


---

## `daemon_threads/daemon_threads.md`

A daemon thread doesn't keep the JVM alive - once every non-daemon thread finishes, the JVM
exits immediately and kills any remaining daemon threads mid-execution, with no cleanup. Must
call `setDaemon(true)` before `start()` (throws `IllegalThreadStateException` after).

`ExecutorService` pools create **non-daemon** threads by default - that's exactly why a
forgotten `executor.shutdown()` keeps an app running forever instead of letting the JVM exit
(see `common_issues/_8_thread_leaks`).


---

## `interrupting_a_thread/interrupting_a_thread.md`

`interrupt()` does NOT stop a thread. It just flips a flag. What happens next depends on what
the thread is doing right now:

- **sleeping/waiting** (`sleep`, `wait`, `join`, `future.get()`) -> wakes up immediately with
  `InterruptedException`. Easy case.
- **doing a plain blocking HTTP/socket call** -> interrupt does NOTHING. Thread stays stuck until
  the response comes back, someone closes the connection, or a timeout you configured kicks in.
- **running normal code** -> nothing happens either, code must check `isInterrupted()` itself.

Takeaway: `interrupt()` only works if the thing you're waiting on was built to listen for it.
Plain HTTP calls aren't - that's why you always set a timeout, so a stuck thread can't hang forever.


---

## `java_concurrent_package/blocking_queue/blocking_queue.md`

A queue built for the producer-consumer pattern: one thread `put()`s items, another `take()`s
them. If the queue is full, `put()` blocks; if it's empty, `take()` blocks - no manual
wait/notify needed.

Common types: `ArrayBlockingQueue` (bounded), `LinkedBlockingQueue` (optionally bounded),
`PriorityBlockingQueue` (orders by comparator), `DelayQueue` (items become available after a delay).


---

## `java_concurrent_package/countdown_latch/countdown_latch.md`

Lets one or more threads wait until N other things finish. Each worker calls `countDown()` when
done; `await()` blocks until the count reaches 0. One-shot - can't be reset or reused once it
hits zero (that's what `CyclicBarrier` is for).


---

## `java_concurrent_package/cyclic_barrier/cyclic_barrier.md`

Makes a fixed number of threads all wait for each other at a common point before any of them
continue - once everyone arrives, they're released together (optionally running one action
first). Unlike `CountDownLatch`, it's **reusable**: the count resets automatically for the next
round, which is why it's called "cyclic".


---

## `java_concurrent_package/exchanger/exchanger.md`

A rendezvous point for exactly two threads to swap objects. Both call `exchange(value)`; each
blocks until the other arrives, then each gets back what the other one passed in. Niche - mostly
seen in producer/consumer buffer-swapping scenarios.


---

## `java_concurrent_package/executor_service/completable_future/completable_future.md`

A more powerful `Future`: instead of blocking on `get()`, you chain steps that run automatically
once the previous one completes (`thenApply`, `thenAccept`...), and you can combine results from
several async calls or handle their exceptions (`exceptionally`) without blocking a thread to wait.

### Not blocking: callbacks instead of get()
`Future` gives you exactly one way to get the result: call `get()` and block until it's ready.
`CompletableFuture` adds a second option: attach a callback (`thenAccept`, etc.) that runs
automatically, on whatever thread finishes the work, whenever it's ready - your own thread never
stops to wait, it just keeps going to its next line immediately. See `CompletableFutureCallbackDemo`:
the timestamps show `main` reaching its own next lines instantly, while the callback fires ~2s
later on a pool thread, after `main` already moved on. Use this when you don't need the result
right there in your current method; use `join()`/`get()` (like `CompletableFutureParallelDemo`
does) when you do need it synchronously, e.g. to print a combined result next.

### Exceptions: handled inline, not just at a blocking checkpoint
With `Future`, the only place you can react to a failure is at `get()`, wrapped in try/catch -
and if you never call `get()`, you never find out it failed (see `future.md`). With
`CompletableFuture`, the "what to do if this fails" step can be part of the chain itself:

- `exceptionally(ex -> fallback)` - only runs on failure, supplies a fallback value, and the
  rest of the chain (`thenApply`, `thenAccept`...) keeps running with it instead of just dying
- `handle((result, ex) -> ...)` - runs on EITHER outcome, one place, check `ex != null` to tell
  which happened

Neither of these blocks anything - the recovery is just another chain step, not a separate
try/catch around a blocking call. See `CompletableFutureExceptionDemo`. (If you skip both and
just call `get()`/`join()` on a failed chain, you get the same `ExecutionException`/
`CompletionException`-wrapped-cause behavior as plain `Future`.)

### Gotcha: it can get silently killed in a short-lived program
`supplyAsync()` with no explicit executor runs on `ForkJoinPool.commonPool()`, whose worker
threads are **daemon threads** (see `daemon_threads`). If nothing else keeps the JVM alive (e.g.
in a plain `main()` demo, not a long-running server), the JVM can exit the instant `main()`
returns - even mid-task - and the async work is abandoned with no error, no log, nothing. That's
why `CompletableFutureDemo` ends with a `Thread.sleep(...)`: without it, `"Final result"` never
prints. In a real server app this isn't an issue, since the app never returns from `main` while
serving requests - but any short-lived program (CLI tool, batch job) needs something to block on.


---

## `java_concurrent_package/executor_service/executor_interface/executor_interface.md`

`Executor` is the base interface - just `execute(Runnable)`, fire and forget, no result, no
shutdown. `ExecutorService` extends it and adds `submit()` (returns a `Future`), task tracking
and lifecycle management (`shutdown()`). In practice you almost always use `ExecutorService`.


---

## `java_concurrent_package/executor_service/executors.md`

ExecutorService manages a pool of threads and hands you an API to submit work to them, instead of
creating raw threads yourself. `submit()` returns a `Future` you can use to get the result later
(`get()` blocks until it's ready) or cancel the task.

Must be shut down when done, or its threads leak:
- `shutdown()` - lets running tasks finish first
- `shutdownNow()` - tries to stop everything immediately, no guarantee


---

## `java_concurrent_package/executor_service/future/future.md`

`Future` is a handle to a result that isn't ready yet. `executor.submit(task)` returns
immediately with a `Future`; the task runs in the background. `future.get()` blocks until the
result is ready (or throws if the task failed). `future.isDone()` checks without blocking.

Downside: `get()` has no way to combine/chain with other futures - that's what
`CompletableFuture` is for.

### Exceptions
If the task throws, the exception is just **captured**, not delivered - `isDone()` becomes
`true` either way (success or failure), so it tells you nothing about whether it succeeded.
The exception only surfaces when you call `get()`, and it's re-thrown **on the calling thread**,
wrapped:

- `ExecutionException` - the task itself threw; real exception is `e.getCause()`
- `InterruptedException` - the calling thread (not the task) was interrupted while blocked in `get()`
- `CancellationException` (unchecked) - the task was cancelled via `future.cancel(true)`

Gotcha: if a task submitted via `submit()` throws and you never call `get()` on its `Future`,
the exception is silently swallowed - no log, no crash, nothing. (Tasks run via `execute()`
instead go to the thread's `UncaughtExceptionHandler` and print a stack trace, so at least
that's visible - `submit()` without `get()` is the dangerous, silent case.) See
`FutureExceptionDemo` for all of this live.


---

## `java_concurrent_package/executor_service/scheduled_executor_service/scheduled_executor_service.md`

Runs tasks after a delay, or repeatedly, without a manual `Timer`/`sleep` loop.

- `schedule(task, delay, unit)` - run once, after a delay
- `scheduleAtFixedRate(task, initialDelay, period, unit)` - period measured from each run's start
- `scheduleWithFixedDelay(task, initialDelay, delay, unit)` - delay measured from each run's end
  (use this if the task's duration can vary and runs shouldn't pile up)


---

## `java_concurrent_package/executor_service/thread_pool_executor/thread_pool_executor.md`

`ThreadPoolExecutor` is the real class behind `Executors.newFixedThreadPool()` etc.

It has: a fixed number of threads always running (core), a queue where extra tasks wait, and a
max limit on threads it can grow to. If the queue AND max threads are both full, it rejects the
task (throws by default).

Gotcha: `newFixedThreadPool()` has an unlimited queue, so it never actually rejects - it just
piles up tasks forever, which can quietly eat all your memory (see
`common_issues/_9_improper_thread_pool_usage`).


---

## `java_concurrent_package/fork_join/forkjoin.md`

A special `ExecutorService` for divide-and-conquer work: split a big task into smaller ones
recursively (`fork`), run them in parallel across CPU cores, then combine the results (`join`).
Use `RecursiveTask<V>` if the task returns a result, `RecursiveAction` if it doesn't.


---

## `java_concurrent_package/phaser/phaser.md`

Like `CyclicBarrier` but for a dynamic number of parties across multiple phases: threads can
`register()`/`arriveAndDeregister()` at runtime, and `arriveAndAwaitAdvance()` moves everyone to
the next phase together. More flexible, also more complex - `CyclicBarrier` covers most real needs.


---

## `java_concurrent_package/semaphore/semaphore.md`

Caps how many threads can use a resource at the same time, using a fixed number of "permits".
`acquire()` takes a permit (blocks/waits if none are free), `release()` gives it back. Unlike a
lock (1 owner at a time), a semaphore can allow several threads in at once.


---

## `java_concurrent_package/thread_factory/thread_factory.md`

`ThreadFactory` is the hook that lets you control how a pool's threads get created, instead of
getting default ones. What people actually use it for:

- **naming** - so logs/thread dumps say `payment-worker-3` instead of `pool-1-thread-3`
- **catching uncaught exceptions** - a task that throws (via `execute()`) normally just dumps a
  stack trace to stderr and vanishes; a factory can attach a proper handler (log it, alert, etc.)
- **daemon flag** - decide if these threads should keep the JVM alive or not

Pass it to an `ExecutorService`, e.g. `Executors.newFixedThreadPool(n, factory)`.


---

## `producer_consumer/producer_consumer.md`

Producer-consumer: one thread adds items to a shared buffer, another removes them, and each side
must wait when the buffer is full (producer) or empty (consumer). Three ways to build it, same
problem, increasing convenience:

- `wait_notify` - manual, using `synchronized` + `wait()`/`notifyAll()`
- `with_reentrant_lock` - manual, using `Lock` + `Condition`
- `with_blocking_queue` - `BlockingQueue` does it for you (use this one in real code)


---

## `producer_consumer/wait_notify/wait_notify.md`

The original, manual way to solve producer-consumer: a shared queue guarded by `synchronized`.
Producer calls `wait()` when the queue is full, consumer calls `wait()` when it's empty; whoever
changes the queue calls `notifyAll()` to wake the other side up. Always call `wait()` in a
`while` loop (not `if`), since a woken thread must re-check the condition still holds.


---

## `producer_consumer/with_blocking_queue/with_blocking_queue.md`

Simplest way to solve producer-consumer: `BlockingQueue` already does the waiting/notifying
internally. `put()` blocks when full, `take()` blocks when empty - no manual lock/condition code
needed. Use this in real code; wait/notify and Lock/Condition are mostly for understanding what's
happening underneath.


---

## `producer_consumer/with_reentrant_lock/with_reentrant_lock.md`

Same idea as wait/notify, but with an explicit `Lock` + `Condition` instead of `synchronized` +
the implicit monitor. `condition.await()`/`signal()` replace `wait()`/`notifyAll()` - the benefit
is you can have separate conditions (e.g. `notFull`, `notEmpty`), so you only wake up the threads
that actually care, instead of waking everyone with `notifyAll()`.


---

## `thread_creation/thread_creation.md`

Three ways to run code on a thread:

- **extends Thread** - simplest, but burns your only superclass slot (Java has no multiple
  inheritance) and mixes "what to run" together with "how to run it"
- **implements Runnable** - preferred: no result, but the class stays free to extend something
  else; pass it to a `Thread` or an `ExecutorService`
- **implements Callable\<V\>** - like `Runnable` but returns a result and can throw checked
  exceptions; only usable via `ExecutorService.submit()`, which hands back a `Future<V>`


---

## `thread_lifecycle/thread_lifecycle.md`

A thread moves through a fixed set of `Thread.State` values:

- **NEW** - created, `start()` not called yet
- **RUNNABLE** - running, or ready and waiting for CPU time (JVM doesn't distinguish the two)
- **BLOCKED** - waiting to enter a `synchronized` block/method someone else holds
- **WAITING** - waiting indefinitely for another thread (`wait()`, `join()` with no timeout)
- **TIMED_WAITING** - same as WAITING but with a timeout (`sleep()`, `wait(ms)`, `join(ms)`)
- **TERMINATED** - `run()` has finished

Run `ThreadLifecycleDemo` to see NEW -> TIMED_WAITING -> BLOCKED -> TERMINATED happen live.


---

## `types_of_thread_executions/blocking_noneBlocking/blocking_noneBlocking.md`

Blocking: the calling thread stops and waits until the operation has something to return
(`queue.take()`, a plain socket read). Non-blocking: it returns immediately, with whatever it has
right now - even "nothing yet" (`queue.poll()`, a socket in non-blocking mode).

Different axis from sync/async: sync/async is about whether the *caller* waits for the *result*;
blocking/non-blocking is about whether a single *call* parks the thread while it works. Run
`BlockingDemo` vs `NonBlockingDemo` - same queue, `take()` vs `poll()`, very different timing.


---

## `types_of_thread_executions/sync_async/sync_async.md`

Sync: caller waits for the result before continuing. Async: caller keeps going, gets the result
later (via callback, `Future`, `CompletableFuture`, a reactive publisher, etc.).

Note `Future.get()` is a middle case: the *work* runs async, but the caller still blocks waiting
for it - so it behaves sync from the caller's point of view. True non-blocking async needs
something like `CompletableFuture`/reactive, where you attach a callback instead of calling `get()`.


---

## `types_of_work/cpu_heavy/cpu_heavy_work.md`

Work that keeps a CPU core busy the whole time - no waiting involved (math, encryption, report
generation). Thread pool size should match the number of CPU cores - more threads than cores just
adds context-switching overhead without speeding anything up.

Run `SequentialTest` vs `ParallelTest` (parallel stream, uses Fork/Join under the hood) to see
the difference on a multi-core machine.


---

## `types_of_work/io_work/io_bound_tasks.md`

Work that mostly waits, not computes - the thread is idle while something else finishes (HTTP
calls, DB queries, file I/O, messaging). Since threads are idle rather than crunching, you can
afford way more of them than CPU cores.

Run `BlockingWaitDemo` (fixed pool of 10 platform threads, 50 tasks) vs `VirtualThreadDemo`
(one virtual thread per task) to see why virtual threads exist: platform threads are expensive
to hold idle in large numbers, virtual threads aren't.


---

## `types_of_work/parallel_tasks/parallel_tasks.md`

One big task split into independent pieces, run at the same time, then combined - not waiting
on I/O, just splitting CPU work (overlaps with cpu_heavy_work). Typical tools: parallel streams
(`ParallelTest` in `cpu_heavy/`) and `ForkJoinPool` (see `java_concurrent_package/fork_join/ForkJoinSumDemo`).


---

## `types_of_work/reactive__event_driven_tasks/reactive__event_driven_tasks.md`

Threads don't sit and wait - they react when something happens (an event listener fires, a
message arrives, a UI click). Between events, no thread is tied up at all.

Examples: event listeners, message consumers (Kafka/Rabbit), UI events, reactive streams
(see `sync_async/AsyncWithReactiveProject`).


---

## `types_of_work/scheduled_delayed_tasks/scheduled_delayed_tasks.md`

Work that runs later or repeatedly instead of right now - cron jobs, retry logic, periodic
cleanup. See `java_concurrent_package/scheduled_executor_service/ScheduledExecutorServiceDemo`
for how to schedule this without a manual `sleep` loop.


---

## `types_of_work/types_of_work.md`

Different kinds of work need different threading strategies - more threads doesn't always help:

- `cpu_heavy` - crunching, no waiting -> threads ~= CPU cores
- `io_work` - mostly waiting (HTTP, DB, files) -> can use way more threads, or virtual threads
- `parallel_tasks` - one task split into pieces and run at once (overlaps with cpu_heavy)
- `reactive__event_driven_tasks` - threads react to events instead of blocking/waiting
- `scheduled_delayed_tasks` - work that runs later or repeatedly, not immediately


---

## `way_to_thread_safety/atomic_objects/atomic_objects.md`

Atomic classes allow us to perform atomic operations, which are thread-safe, without using synchronization. 
An atomic operation is executed in one single machine-level operation.

---

## `way_to_thread_safety/concurrent_collections/concurrent_collections.md`

Thread-safe collections built for concurrent use, without wrapping every call in your own lock:

- `ConcurrentHashMap` - locks only a small segment on write, not the whole map (see
  `common_issues/_10_concurrency_bugs_data_structures/map`)
- `CopyOnWriteArrayList` - copies the whole array on write, readers never block (see demo here)
- `BlockingQueue` - built-in waiting for producer/consumer (see `java_concurrent_package/blocking_queue`)


---

## `way_to_thread_safety/double_checked_locking/double_checked_locking.md`

Classic interview combo: thread-safe lazy singleton without paying a lock on every call.
Check-then-lock-then-check-again: the first check (no lock) is the fast path once the instance
exists; the second check (inside the lock) catches the case where another thread created it
while we were waiting for the lock.

`instance` **must** be `volatile`: `new LazySingleton()` isn't one atomic step (allocate memory,
run the constructor, assign the reference) - without `volatile`, another thread could see a
half-constructed object due to instruction reordering. See `way_to_thread_safety/volatile_keyword`.


---

## `way_to_thread_safety/immutable_implementations/immutable_implementations.md`

The simplest way to be thread-safe: have no mutable shared state at all. If an object's fields
are `final` and set once in the constructor, there's nothing for two threads to race over - no
locks, no atomics, no synchronization needed anywhere.


---

## `way_to_thread_safety/locks/locks.md`

`Lock` is a more flexible alternative to `synchronized`: supports `tryLock()` (don't block if
busy), `lockInterruptibly()`, and the lock/unlock can happen in different methods (`synchronized`
must start and end in the same block).

- `reentrant/reentrant_locks` - direct `synchronized` replacement, same thread can re-acquire it
- `reentrant/reentrant_read_write_locks` - many readers at once, but only one writer, never both
- `stamped_locks/stamped_lock` - faster non-reentrant alternative, unlocked via a returned stamp
- `stamped_locks/stamped_with_optimistic_lock` - readers don't block writers at all, just verify
  after reading that no write happened; retry with a real read lock if one did

(For a Condition-based producer/consumer example, see `producer_consumer/with_reentrant_lock`.)


---

## `way_to_thread_safety/locks/reentrant/reentrant_locks/reentrant_locks.md`

`ReentrantLock` behaves like `synchronized` (one owner at a time, same thread can re-lock it
without deadlocking itself) but as an explicit object: `lock()`/`unlock()` instead of a block,
plus extras like `tryLock()` and fair ordering (`new ReentrantLock(true)`).


---

## `way_to_thread_safety/locks/reentrant/reentrant_read_write_locks/reentrant_read_write_locks.md`

Splits the lock in two: any number of readers can hold the **read** lock at once (reads don't
conflict), but the **write** lock is exclusive - no readers or other writers allowed while
someone's writing. Good when reads vastly outnumber writes; with mostly writes, a plain
`ReentrantLock` is simpler and just as fast.


---

## `way_to_thread_safety/locks/stamped_locks/stamped_lock/stamped_lock.md`

Like `ReentrantReadWriteLock` but faster, at the cost of two catches: it's **not reentrant**
(locking twice on the same thread deadlocks), and `unlock()` needs the exact `long` stamp
returned by `lock()` - mixing up read/write stamps silently breaks the locking.


---

## `way_to_thread_safety/locks/stamped_locks/stamped_with_optimistic_lock/stamped_with_optimistic_lock.md`

`tryOptimisticRead()` takes no lock at all - just read the value, then call `validate(stamp)`
to check whether a writer changed it in the meantime. If validation fails, fall back to a real
`readLock()` and read again. Best when reads vastly outnumber writes and writes are rare enough
that retrying occasionally is cheaper than every reader always taking a lock.


---

## `way_to_thread_safety/semaphore/semaphore.md`

Caps how many threads can use a resource at once, using a fixed number of permits.
`acquire()` takes a permit (blocks if none free), `release()` gives it back. Unlike a lock
(1 owner at a time), several threads can hold permits at the same time - useful for capping
concurrent access to something like a connection pool or a rate-limited API.


---

## `way_to_thread_safety/stateless_implementations/stateless_implementations.md`

A method/class with no fields (or only `static final` constants) is automatically thread-safe:
the result depends only on its arguments, and nothing is shared between calls for two threads to
race over. Prefer this over immutability when you don't even need an object - just static methods.


---

## `way_to_thread_safety/synchronized_collections/syncCollections.md`

`Collections.synchronizedXxx(collection)` wraps every method call with a lock on one shared
object - correct, but the whole collection is locked for every access, so only one thread can
use it at a time. `ConcurrentHashMap`/`CopyOnWriteArrayList` scale better because they don't
lock everything at once (see `concurrent_collections`).


---

## `way_to_thread_safety/synchronized_keyword/synchronized.md`

`synchronized` (on a method or a block) makes sure only one thread at a time can run that code -
it uses a monitor lock tied to an object (every object has one, "intrinsic lock"). A synchronized
instance method locks on `this`; a synchronized block locks on whatever object you pass it.

Prefer locking on a dedicated private `Object`, not `this` or a String/boxed number - those can
be shared/interned elsewhere in the JVM, so an attacker (or unrelated code) could lock on the
same object and cause a deadlock.

**happens-before**: the formal guarantee behind all of this. Everything a thread does before
`unlock()` is guaranteed visible to the next thread that `lock()`s the same monitor - that's why
`synchronized` fixes both visibility AND atomicity, not just mutual exclusion. `volatile`
writes/reads have the same happens-before guarantee for that one variable (see `volatile_keyword`).


---

## `way_to_thread_safety/thread_local_variables/thread_local_variables.md`

`ThreadLocal<T>` gives every thread its own independent copy of a variable - `get()`/`set()`
never touch another thread's value, so there's nothing to synchronize. Common use: per-thread
DB connections, or holding a request's user/context in a web server without passing it through
every method call.

Gotcha: in thread pools, threads are reused - always `remove()` when done, or the next task
on that thread inherits stale data (a common cause of leaks in Spring MDC/security context use).


---

## `way_to_thread_safety/volatile_keyword/volatile.md`

`volatile` forces every read/write of that field to go to main memory instead of a thread's
cached copy - so a change made by one thread is guaranteed visible to others immediately.

It only fixes **visibility**, not atomicity: `volatile int x; x++;` can still race, because
increment is read-modify-write (multiple steps). For that you still need `synchronized` or an
`Atomic*` class (see `atomic_objects`). Use `volatile` for simple flags/published values written
by one thread and read by others - see `VolatileDemo` here, and the broken version (no `volatile`)
in `common_issues/_6_memory_consistency_volatile`.

Formally this is the JMM's **happens-before** guarantee: a write to a `volatile` field
happens-before every later read of that same field by any thread - and everything written
*before* that write (even non-volatile fields) becomes visible too, not just the flag itself.
`synchronized` gives the same guarantee across `unlock()`/`lock()` (see `synchronized_keyword`).


---

## `way_to_thread_safety/way_to_thread_safety.md`

`common_issues` is the problem side (what goes wrong); this package is the solution side - ways
to actually make code thread-safe, roughly cheapest/simplest first:

- `stateless_implementations` / `immutable_implementations` - avoid shared mutable state entirely
- `thread_local_variables` - give each thread its own copy instead of sharing
- `atomic_objects` - lock-free atomic operations for single variables
- `volatile_keyword` - visibility only, not atomicity
- `synchronized_keyword` / `locks` - mutual exclusion, block other threads out
- `synchronized_collections` / `concurrent_collections` - thread-safe collections instead of
  wrapping your own
- `semaphore` - cap concurrent access instead of fully excluding it
- `double_checked_locking` - combines `volatile` + `synchronized` for a thread-safe lazy singleton

