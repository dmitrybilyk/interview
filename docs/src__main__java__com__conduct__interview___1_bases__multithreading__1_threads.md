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

  Concurrency vs Parallelism
- Concurrency is about managing multiple tasks at once
- Parallelism is about executing tasks simultaneously (on multiple cores)
