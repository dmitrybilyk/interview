# Multithreading

---

## Threads

A thread is a unit of execution within a process.
In Java, threads are created by the JVM and mapped to native OS threads.

Threads are lightweight compared to processes because they share the same heap memory,
but each thread has its own stack.

This shared memory model allows efficient communication between threads,
but also introduces concurrency issues such as:
- race conditions,
- visibility problems,
- and deadlocks.

Threads are used to achieve concurrency and improve performance:
- better CPU utilization (parallel execution)
- non-blocking behavior (e.g., handling I/O)
- responsiveness (UI, APIs)

**Concurrency vs Parallelism**
- Concurrency is about managing multiple tasks at once
- Parallelism is about executing tasks simultaneously (on multiple cores)

---

## Sync/Async vs Blocking/Non-Blocking

Synchronous vs asynchronous describes whether the caller waits for the result,
while blocking vs non-blocking describes what happens to the thread.

For example, Spring MVC uses a synchronous blocking model,
where each request is handled by a thread that waits during I/O.

In contrast, reactive systems use asynchronous non-blocking execution,
where threads are not blocked and can handle many requests.

**Sync/Async = waiting model
Blocking/Non-blocking = thread behavior**

---

## Types of Thread Executions

### Sync vs Async

Caller thread doesn't wait for result (async).

---

## Types of Work

### CPU-Heavy Work

- massive math calculations
- reports generations
- encryption of data

Number of threads should be the same as number of cores in the CPU.

### I/O-Bound Tasks

Threads are mostly waiting, wasting their time.

- HTTP calls
- database queries
- file reading/writing
- messaging (Rabbit, Kafka)

### Parallel Tasks

- Fork-Join-Pool
- parallel streams

### Reactive / Event-Driven Tasks

Threads are reacting on some event.

- event listeners
- message consumers
- ui events

### Scheduled / Delayed Tasks

- cron jobs
- retry logic

---

## Common Issues

### 1. Race Conditions
- **Issue**: Threads access shared resources without synchronization.
- **Solution**: Use `synchronized`, locks, or atomic classes.

### 2. Deadlocks
- **Issue**: Threads wait on each other, causing a stuck state.
- **Solution**: Avoid nested locks or use `tryLock()`.

Deadlock is the situation when 2 threads are waiting for the lock
to be open. One thread occupies lock on objA and tries to get the lock
on objB and another thread enters lock of objB and tries to get lock on
objA.
Can be fixed with putting the locking into the same order.

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
Volatile solves visibility issue when single thread writes. If multiple threads write then
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
We should always shutdown executors to avoid leaking as well.

### 9. Improper Thread Pool Use
- **Issue**: Too few or too many threads in a pool impacts performance.
- **Solution**: Choose suitable thread pool sizes.
In case of CPU operations (heavy calculations etc.) thread pool size could be equal to
number CPU cores to gradually split load between cores. In case of IO operations (http calls, db call etc)
thread pool size should be calculated with taking into account waiting time, so would be 10 times bigger.

### 10. Concurrency Bugs in Data Structures
- **Issue**: Concurrent data structure use causes issues.
- **Solution**: Use thread-safe collections like `ConcurrentHashMap`.

### 11. Fork/Join Pool Misuse
- **Issue**: Task imbalance leads to poor performance.
- **Solution**: Divide tasks evenly and follow Fork/Join patterns.
Misusing the Fork/Join pool occurs when you execute blocking I/O operations or choose an inefficient task threshold,
which leads to thread starvation and excessive context-switching overhead.

---

## Ways to Achieve Thread Safety

### synchronized Keyword

Synchronized keyword (on method, static method or block of code) helps to avoid race condition.
Synchronization is achieved with the help of monitor — mechanism to achieve mutual exclusion (just
one thread can execute in the critical part), conditional executions with notifications.
There is a lock object associated with every object or class called mutex (binary semaphore, intrinsic locking).
It's better to use an external object for locking for the security (attacker can cause a deadlock and Denial Of
Service in case of using `this`).
It's better to avoid Strings as mutex because of String pool. The same for Integer, Long pool.

There are two main problems in multithreading:
- visibility (two threads can't predict what other thread can do)
- accessibility (two threads try to access the same resource at the same time)

### volatile Keyword

With the volatile keyword, we instruct the JVM and the compiler to store
the variable in the main memory.
Moreover, the use of a volatile variable ensures that all variables that are visible to a given thread
will be read from the main memory as well.

### Atomic Objects

Atomic classes allow us to perform atomic operations, which are thread-safe, without using synchronization.
An atomic operation is executed in one single machine-level operation.

### Locks

Locks are more flexible. They allow several threads to get inside
the critical section in case they just wanted to read. You can
`tryLock`, `lockInterruptibly`, and a critical section can start in one method
and end in another.

Reentrant lock allows the same thread to enter the critical section
several times (with count usage). The fairness feature allows to build
a priority based on how much time threads are waiting for the lock.

Stamped locks are not reentrant; a stamp long value is used to unlock
the lock in a more safe way, though one should be careful not to escape the stamp.
Stamped locks provide optimistic locking when a reader can read regardless
of whether a writing operation is done or not, to be able to see current results.

### Synchronized Collections

Wrapper method to make collection synchronized is used.
It's not that performant.

### Concurrent Collections

Unlike their synchronized counterparts, concurrent collections achieve thread-safety
by dividing their data into segments.
- `ConcurrentHashMap`
- `CopyOnWriteArrayList`
- `BlockingQueue`

Segmented means: instead of locking the entire collection, only a small part (segment) of the data
is locked or copied when accessed or modified. This avoids blocking the whole structure and allows
multiple threads to work in parallel on different parts.

---

## java.util.concurrent Package

### ExecutorService

ExecutorService is a JDK API that simplifies running tasks in asynchronous mode. Generally speaking,
ExecutorService automatically provides a pool of threads and an API for assigning tasks to it.

`execute` method allows to run Runnables; `submit` and `invoke`-like methods return
Future objects. Calling `get()` on the Future blocks execution and
returns the actual result for a Callable task and null for a Runnable.

Future tasks can be checked and cancelled.

ExecutorService should be stopped:
- with `shutdown` — gives running threads the ability to complete
- with `shutdownNow` — tries to stop all running tasks but there is no guarantee

### CompletableFuture

A wrapper around the Future object which is more powerful. The purpose is to execute async code effectively.
There is an ability to combine results of several threads, handle exceptions, etc.

### Fork/Join Pool

Fork/Join pool is a feature which allows to fork a task into many tasks,
execute them across many cores (threads) and then join the result at the end.
It's a special implementation of ExecutorService which uses recursion
and the principle of divide and conquer.

### BlockingQueue

A data structure which allows implementing the producer-consumer paradigm.
Two threads can share the queue: one can add the message, another can take.
Can be unbounded, bounded, priority (with a comparator), delayed queue, etc.

### Semaphore

Semaphore allows restricting the number of threads which can get inside
the resource with help of a permits value.
