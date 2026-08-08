A more powerful `Future`: instead of blocking on `get()`, you chain steps that run automatically
once the previous one completes (`thenApply`, `thenAccept`...), and you can combine results from
several async calls or handle their exceptions (`exceptionally`) without blocking a thread to wait.
