A daemon thread doesn't keep the JVM alive - once every non-daemon thread finishes, the JVM
exits immediately and kills any remaining daemon threads mid-execution, with no cleanup. Must
call `setDaemon(true)` before `start()` (throws `IllegalThreadStateException` after).

`ExecutorService` pools create **non-daemon** threads by default - that's exactly why a
forgotten `executor.shutdown()` keeps an app running forever instead of letting the JVM exit
(see `common_issues/_8_thread_leaks`).
