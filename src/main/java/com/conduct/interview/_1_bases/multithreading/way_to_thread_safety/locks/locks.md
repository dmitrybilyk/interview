`Lock` is a more flexible alternative to `synchronized`: supports `tryLock()` (don't block if
busy), `lockInterruptibly()`, and the lock/unlock can happen in different methods (`synchronized`
must start and end in the same block).

- `reentrant/reentrant_locks` - direct `synchronized` replacement, same thread can re-acquire it
- `reentrant/reentrant_read_write_locks` - many readers at once, but only one writer, never both
- `stamped_locks/stamped_lock` - faster non-reentrant alternative, unlocked via a returned stamp
- `stamped_locks/stamped_with_optimistic_lock` - readers don't block writers at all, just verify
  after reading that no write happened; retry with a real read lock if one did

(For a Condition-based producer/consumer example, see `producer_consumer/with_reentrant_lock`.)
