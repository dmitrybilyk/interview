`volatile` forces every read/write of that field to go to main memory instead of a thread's
cached copy - so a change made by one thread is guaranteed visible to others immediately.

It only fixes **visibility**, not atomicity: `volatile int x; x++;` can still race, because
increment is read-modify-write (multiple steps). For that you still need `synchronized` or an
`Atomic*` class (see `atomic_objects`). Use `volatile` for simple flags/published values written
by one thread and read by others - see `VolatileDemo` here, and the broken version (no `volatile`)
in `common_issues/_6_memory_consistency_volatile`.

Formally this is the JMM's **happens-before** guarantee: a write to a `volatile` field
happens-before every later read of that same field by any thread - and everything written
*before* that write (even non-volatile fields) becomes visible too, not just the flag itself.
`synchronized` gives the same guarantee across `unlock()`/`lock()` (see `synchronized_keyword`).
