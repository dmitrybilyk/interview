`ReentrantLock` behaves like `synchronized` (one owner at a time, same thread can re-lock it
without deadlocking itself) but as an explicit object: `lock()`/`unlock()` instead of a block,
plus extras like `tryLock()` and fair ordering (`new ReentrantLock(true)`).
