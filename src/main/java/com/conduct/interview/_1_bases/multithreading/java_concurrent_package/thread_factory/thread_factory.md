`ThreadFactory` is the hook that lets you control how a pool's threads get created, instead of
getting default ones. What people actually use it for:

- **naming** - so logs/thread dumps say `payment-worker-3` instead of `pool-1-thread-3`
- **catching uncaught exceptions** - a task that throws (via `execute()`) normally just dumps a
  stack trace to stderr and vanishes; a factory can attach a proper handler (log it, alert, etc.)
- **daemon flag** - decide if these threads should keep the JVM alive or not

Pass it to an `ExecutorService`, e.g. `Executors.newFixedThreadPool(n, factory)`.
