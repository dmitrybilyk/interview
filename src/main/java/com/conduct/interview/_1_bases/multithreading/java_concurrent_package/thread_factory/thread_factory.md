A factory for creating threads with custom settings (name, daemon flag, priority) instead of
default ones. Usually passed to an `ExecutorService` so its pool threads get readable names -
handy for logs/thread dumps instead of generic "pool-1-thread-1".
