A queue built for the producer-consumer pattern: one thread `put()`s items, another `take()`s
them. If the queue is full, `put()` blocks; if it's empty, `take()` blocks - no manual
wait/notify needed.

Common types: `ArrayBlockingQueue` (bounded), `LinkedBlockingQueue` (optionally bounded),
`PriorityBlockingQueue` (orders by comparator), `DelayQueue` (items become available after a delay).
