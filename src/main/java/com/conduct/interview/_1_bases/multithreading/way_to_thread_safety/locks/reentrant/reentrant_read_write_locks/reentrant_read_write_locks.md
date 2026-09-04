Splits the lock in two: any number of readers can hold the **read** lock at once (reads don't
conflict), but the **write** lock is exclusive - no readers or other writers allowed while
someone's writing. Good when reads vastly outnumber writes; with mostly writes, a plain
`ReentrantLock` is simpler and just as fast.
