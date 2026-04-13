Deadlock is the situation when 2 threads are waiting for the lock
to be open. One thread occupies lock on objA and tries to get the lock
on objB and another thread enters lock of objB and tries to get lock on
objA. 
Can be fixed with putting the locking into the same order.