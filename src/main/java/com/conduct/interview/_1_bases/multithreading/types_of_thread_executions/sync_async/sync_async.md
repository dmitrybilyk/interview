Sync: caller waits for the result before continuing. Async: caller keeps going, gets the result
later (via callback, `Future`, `CompletableFuture`, a reactive publisher, etc.).

Note `Future.get()` is a middle case: the *work* runs async, but the caller still blocks waiting
for it - so it behaves sync from the caller's point of view. True non-blocking async needs
something like `CompletableFuture`/reactive, where you attach a callback instead of calling `get()`.
