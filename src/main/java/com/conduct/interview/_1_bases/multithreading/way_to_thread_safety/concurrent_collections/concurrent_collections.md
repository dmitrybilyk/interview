Thread-safe collections built for concurrent use, without wrapping every call in your own lock:

- `ConcurrentHashMap` - locks only a small segment on write, not the whole map (see
  `common_issues/_10_concurrency_bugs_data_structures/map`)
- `CopyOnWriteArrayList` - copies the whole array on write, readers never block (see demo here)
- `BlockingQueue` - built-in waiting for producer/consumer (see `java_concurrent_package/blocking_queue`)
