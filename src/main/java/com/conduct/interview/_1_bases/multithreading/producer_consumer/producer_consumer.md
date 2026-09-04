Producer-consumer: one thread adds items to a shared buffer, another removes them, and each side
must wait when the buffer is full (producer) or empty (consumer). Three ways to build it, same
problem, increasing convenience:

- `wait_notify` - manual, using `synchronized` + `wait()`/`notifyAll()`
- `with_reentrant_lock` - manual, using `Lock` + `Condition`
- `with_blocking_queue` - `BlockingQueue` does it for you (use this one in real code)
