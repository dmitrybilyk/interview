ExecutorService manages a pool of threads and hands you an API to submit work to them, instead of
creating raw threads yourself. `submit()` returns a `Future` you can use to get the result later
(`get()` blocks until it's ready) or cancel the task.

Must be shut down when done, or its threads leak:
- `shutdown()` - lets running tasks finish first
- `shutdownNow()` - tries to stop everything immediately, no guarantee
