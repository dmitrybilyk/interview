Like `CyclicBarrier` but for a dynamic number of parties across multiple phases: threads can
`register()`/`arriveAndDeregister()` at runtime, and `arriveAndAwaitAdvance()` moves everyone to
the next phase together. More flexible, also more complex - `CyclicBarrier` covers most real needs.
