`Future` is a handle to a result that isn't ready yet. `executor.submit(task)` returns
immediately with a `Future`; the task runs in the background. `future.get()` blocks until the
result is ready (or throws if the task failed). `future.isDone()` checks without blocking.

Downside: `get()` has no way to combine/chain with other futures - that's what
`CompletableFuture` is for.
