`Executor` is the base interface - just `execute(Runnable)`, fire and forget, no result, no
shutdown. `ExecutorService` extends it and adds `submit()` (returns a `Future`), task tracking
and lifecycle management (`shutdown()`). In practice you almost always use `ExecutorService`.
