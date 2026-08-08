Simplest way to solve producer-consumer: `BlockingQueue` already does the waiting/notifying
internally. `put()` blocks when full, `take()` blocks when empty - no manual lock/condition code
needed. Use this in real code; wait/notify and Lock/Condition are mostly for understanding what's
happening underneath.
