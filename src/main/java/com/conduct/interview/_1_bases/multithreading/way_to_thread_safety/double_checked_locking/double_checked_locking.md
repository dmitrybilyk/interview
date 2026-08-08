Classic interview combo: thread-safe lazy singleton without paying a lock on every call.
Check-then-lock-then-check-again: the first check (no lock) is the fast path once the instance
exists; the second check (inside the lock) catches the case where another thread created it
while we were waiting for the lock.

`instance` **must** be `volatile`: `new LazySingleton()` isn't one atomic step (allocate memory,
run the constructor, assign the reference) - without `volatile`, another thread could see a
half-constructed object due to instruction reordering. See `way_to_thread_safety/volatile_keyword`.
