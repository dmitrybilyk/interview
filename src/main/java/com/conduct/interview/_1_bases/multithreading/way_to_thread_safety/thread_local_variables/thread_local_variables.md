`ThreadLocal<T>` gives every thread its own independent copy of a variable - `get()`/`set()`
never touch another thread's value, so there's nothing to synchronize. Common use: per-thread
DB connections, or holding a request's user/context in a web server without passing it through
every method call.

Gotcha: in thread pools, threads are reused - always `remove()` when done, or the next task
on that thread inherits stale data (a common cause of leaks in Spring MDC/security context use).
