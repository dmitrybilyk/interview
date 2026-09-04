Caps how many threads can use a resource at the same time, using a fixed number of "permits".
`acquire()` takes a permit (blocks/waits if none are free), `release()` gives it back. Unlike a
lock (1 owner at a time), a semaphore can allow several threads in at once.
