Synchronized keyword (on method, static method or block of code) helps to avoid race condition.
Synchronization is achieved with the help of monitor - mechanism to achieve mutual exclusion (just 
one thread can execute in the critical part), conditional executions with notifications.
There is a lock object associated with every object or class called mutex (binary semaphore, intrinsic locking).
It's better to appUser external object for locking  for the security (attacker can cause a deadlock and Denial Of
Service in case of using 'this').
It's better to avoid Strings as mutex because of String pool. the same for Integer, Long pool.

There are two main problems in the multithreading:
- visibility (two threads can't predict what other thread can do)
- accessibility (two thread try to access the same resource at the same time)