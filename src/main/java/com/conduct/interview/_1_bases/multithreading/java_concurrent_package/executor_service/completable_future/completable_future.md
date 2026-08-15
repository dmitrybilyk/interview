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
