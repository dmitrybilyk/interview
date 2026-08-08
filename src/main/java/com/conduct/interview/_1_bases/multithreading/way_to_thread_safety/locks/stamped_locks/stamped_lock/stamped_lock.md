Like `ReentrantReadWriteLock` but faster, at the cost of two catches: it's **not reentrant**
(locking twice on the same thread deadlocks), and `unlock()` needs the exact `long` stamp
returned by `lock()` - mixing up read/write stamps silently breaks the locking.
