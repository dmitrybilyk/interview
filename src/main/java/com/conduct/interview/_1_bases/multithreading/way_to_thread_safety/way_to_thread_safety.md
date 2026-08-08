`common_issues` is the problem side (what goes wrong); this package is the solution side - ways
to actually make code thread-safe, roughly cheapest/simplest first:

- `stateless_implementations` / `immutable_implementations` - avoid shared mutable state entirely
- `thread_local_variables` - give each thread its own copy instead of sharing
- `atomic_objects` - lock-free atomic operations for single variables
- `volatile_keyword` - visibility only, not atomicity
- `synchronized_keyword` / `locks` - mutual exclusion, block other threads out
- `synchronized_collections` / `concurrent_collections` - thread-safe collections instead of
  wrapping your own
- `semaphore` - cap concurrent access instead of fully excluding it
- `double_checked_locking` - combines `volatile` + `synchronized` for a thread-safe lazy singleton
