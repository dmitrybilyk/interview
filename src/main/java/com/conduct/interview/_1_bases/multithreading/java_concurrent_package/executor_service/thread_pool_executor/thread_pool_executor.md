`ThreadPoolExecutor` is the real class behind `Executors.newFixedThreadPool()` etc.

It has: a fixed number of threads always running (core), a queue where extra tasks wait, and a
max limit on threads it can grow to. If the queue AND max threads are both full, it rejects the
task (throws by default).

Gotcha: `newFixedThreadPool()` has an unlimited queue, so it never actually rejects - it just
piles up tasks forever, which can quietly eat all your memory (see
`common_issues/_9_improper_thread_pool_usage`).
