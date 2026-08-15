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
