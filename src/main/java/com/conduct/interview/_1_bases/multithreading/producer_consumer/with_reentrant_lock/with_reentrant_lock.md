Same idea as wait/notify, but with an explicit `Lock` + `Condition` instead of `synchronized` +
the implicit monitor. `condition.await()`/`signal()` replace `wait()`/`notifyAll()` - the benefit
is you can have separate conditions (e.g. `notFull`, `notEmpty`), so you only wake up the threads
that actually care, instead of waking everyone with `notifyAll()`.
