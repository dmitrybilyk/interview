# Combining Streams

- **`concat`**: subscribes to sources one after another, in order - the
  second source doesn't even start until the first completes.
- **`merge`**: subscribes to all sources immediately and interleaves
  whatever arrives, as it arrives - no ordering guarantee.
- **`zip`**: pairs up the *n*-th item of each source into a tuple; it
  waits for the slowest source before emitting each pair, and completes
  as soon as the shortest source completes.
