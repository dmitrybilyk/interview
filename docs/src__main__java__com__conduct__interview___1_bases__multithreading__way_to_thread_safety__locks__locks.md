Locks are more flexible. they allow for several thread to get inside
the critical section in case they just wanted to read. You can
`tryLock`, `lockInterrupidly`, critical section can start in one method
and end in another.

Reentrant lock allows the same thread to enter the critical section 
several times (with count usage). The fairness feature allows to build
a priority based on how much time threads are waiting for the lock.

Stamped locks are not reentrant, stamp long value is used to unlock
the lock in more safe way though should be careful not to escape the stamp.
Stamped locks provide the optimistic locking when reader can read regardless 
of writing operation is done or not. To be able to see current results.
