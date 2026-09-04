The original, manual way to solve producer-consumer: a shared queue guarded by `synchronized`.
Producer calls `wait()` when the queue is full, consumer calls `wait()` when it's empty; whoever
changes the queue calls `notifyAll()` to wake the other side up. Always call `wait()` in a
`while` loop (not `if`), since a woken thread must re-check the condition still holds.
