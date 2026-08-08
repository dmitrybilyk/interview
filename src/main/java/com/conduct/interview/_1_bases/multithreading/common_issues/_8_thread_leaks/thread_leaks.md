Thread leak: a thread is created but never finishes and never gets cleaned up, so it keeps
eating memory until the app crashes (`OutOfMemoryError: unable to create new native thread`).

Fix: don't spawn raw `new Thread(...)` per request/task - use an `ExecutorService` instead.
But the `ExecutorService` itself leaks the same way if you never call `shutdown()` on it - its
threads stay alive forever too. Also make sure blocking calls (network, DB, queue) have timeouts,
so a thread can't get stuck waiting forever.
