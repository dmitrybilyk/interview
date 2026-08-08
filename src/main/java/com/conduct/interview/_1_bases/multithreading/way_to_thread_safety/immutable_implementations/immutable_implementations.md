The simplest way to be thread-safe: have no mutable shared state at all. If an object's fields
are `final` and set once in the constructor, there's nothing for two threads to race over - no
locks, no atomics, no synchronization needed anywhere.
