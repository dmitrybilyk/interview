`synchronized` (on a method or a block) makes sure only one thread at a time can run that code -
it uses a monitor lock tied to an object (every object has one, "intrinsic lock"). A synchronized
instance method locks on `this`; a synchronized block locks on whatever object you pass it.

Prefer locking on a dedicated private `Object`, not `this` or a String/boxed number - those can
be shared/interned elsewhere in the JVM, so an attacker (or unrelated code) could lock on the
same object and cause a deadlock.

**happens-before**: the formal guarantee behind all of this. Everything a thread does before
`unlock()` is guaranteed visible to the next thread that `lock()`s the same monitor - that's why
`synchronized` fixes both visibility AND atomicity, not just mutual exclusion. `volatile`
writes/reads have the same happens-before guarantee for that one variable (see `volatile_keyword`).
