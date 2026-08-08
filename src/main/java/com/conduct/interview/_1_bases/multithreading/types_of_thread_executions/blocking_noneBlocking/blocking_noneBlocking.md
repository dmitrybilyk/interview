Blocking: the calling thread stops and waits until the operation has something to return
(`queue.take()`, a plain socket read). Non-blocking: it returns immediately, with whatever it has
right now - even "nothing yet" (`queue.poll()`, a socket in non-blocking mode).

Different axis from sync/async: sync/async is about whether the *caller* waits for the *result*;
blocking/non-blocking is about whether a single *call* parks the thread while it works. Run
`BlockingDemo` vs `NonBlockingDemo` - same queue, `take()` vs `poll()`, very different timing.
