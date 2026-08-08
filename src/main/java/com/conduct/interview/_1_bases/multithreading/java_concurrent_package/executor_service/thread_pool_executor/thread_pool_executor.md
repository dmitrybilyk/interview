`ThreadPoolExecutor` is what `Executors.newFixedThreadPool()` etc. actually build underneath.
Four knobs control it:

- **core pool size** - threads always kept alive, even when idle
- **max pool size** - hard ceiling on threads, only grows past core size once the queue is full
- **work queue** - holds tasks once core threads are busy, before spinning up more (up to max)
- **RejectedExecutionHandler** - kicks in once BOTH the queue and max threads are full:
  - `AbortPolicy` (default) - throws `RejectedExecutionException`
  - `CallerRunsPolicy` - runs the task on the calling thread itself (built-in backpressure)
  - `DiscardPolicy` / `DiscardOldestPolicy` - silently drop the new/oldest task

`Executors.newFixedThreadPool()` uses an **unbounded** queue - that's exactly why unrestrained
task submission through it can quietly exhaust memory instead of ever rejecting anything (see
`common_issues/_9_improper_thread_pool_usage`).
