`tryOptimisticRead()` takes no lock at all - just read the value, then call `validate(stamp)`
to check whether a writer changed it in the meantime. If validation fails, fall back to a real
`readLock()` and read again. Best when reads vastly outnumber writes and writes are rare enough
that retrying occasionally is cheaper than every reader always taking a lock.
