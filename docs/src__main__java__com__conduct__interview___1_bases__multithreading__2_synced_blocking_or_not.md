Synchronous vs asynchronous describes whether the caller waits for the result,
while blocking vs non-blocking describes what happens to the thread.

For example, Spring MVC uses a synchronous blocking model,
where each request is handled by a thread that waits during I/O.

In contrast, reactive systems use asynchronous non-blocking execution,
where threads are not blocked and can handle many requests.

**Sync/Async = waiting model
Blocking/Non-blocking = thread behavior**