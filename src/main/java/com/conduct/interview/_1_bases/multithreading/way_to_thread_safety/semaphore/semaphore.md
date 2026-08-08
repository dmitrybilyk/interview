Caps how many threads can use a resource at once, using a fixed number of permits.
`acquire()` takes a permit (blocks if none free), `release()` gives it back. Unlike a lock
(1 owner at a time), several threads can hold permits at the same time - useful for capping
concurrent access to something like a connection pool or a rate-limited API.
