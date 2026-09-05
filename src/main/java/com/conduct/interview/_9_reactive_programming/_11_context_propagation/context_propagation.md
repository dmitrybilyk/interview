# Context Propagation

Thread-locals don't survive a `publishOn`/`subscribeOn` thread switch, so
Reactor has its own immutable, per-subscription key/value store called
`Context` (e.g. useful for carrying a request/trace ID through an async
chain). Two rules make it work correctly:

- **`contextWrite(ctx -> ctx.put(...))`** only makes values visible to
  operators **upstream** of it in the chain - context flows from
  subscriber to publisher (upstream), opposite to data. In practice, put
  `contextWrite` at (or near) the end of the chain so everything above it
  can read the value.
- **`Mono.deferContextual(ctx -> ...)`** (or `.deferContextual` /
  `.transformDeferredContextual`) is how an operator actually *reads* the
  current context - you can't just call a static "get context" method,
  since the context is scoped to one subscription.
