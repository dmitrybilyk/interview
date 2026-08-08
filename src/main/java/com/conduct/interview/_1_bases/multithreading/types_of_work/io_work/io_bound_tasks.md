Work that mostly waits, not computes - the thread is idle while something else finishes (HTTP
calls, DB queries, file I/O, messaging). Since threads are idle rather than crunching, you can
afford way more of them than CPU cores.

Run `BlockingWaitDemo` (fixed pool of 10 platform threads, 50 tasks) vs `VirtualThreadDemo`
(one virtual thread per task) to see why virtual threads exist: platform threads are expensive
to hold idle in large numbers, virtual threads aren't.
