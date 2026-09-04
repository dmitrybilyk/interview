A thread moves through a fixed set of `Thread.State` values:

- **NEW** - created, `start()` not called yet
- **RUNNABLE** - running, or ready and waiting for CPU time (JVM doesn't distinguish the two)
- **BLOCKED** - waiting to enter a `synchronized` block/method someone else holds
- **WAITING** - waiting indefinitely for another thread (`wait()`, `join()` with no timeout)
- **TIMED_WAITING** - same as WAITING but with a timeout (`sleep()`, `wait(ms)`, `join(ms)`)
- **TERMINATED** - `run()` has finished

Run `ThreadLifecycleDemo` to see NEW -> TIMED_WAITING -> BLOCKED -> TERMINATED happen live.
