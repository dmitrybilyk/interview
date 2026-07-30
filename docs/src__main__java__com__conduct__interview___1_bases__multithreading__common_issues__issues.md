# Common Java Multithreading Issues

### 1. Race Conditions
- **Issue**: Threads access shared resources without synchronization.
- **Solution**: Use `synchronized`, locks, or atomic classes.

### 2. Deadlocks
- **Issue**: Threads wait on each other, causing a stuck state.
- **Solution**: Avoid nested locks or use `tryLock()`.

### 3. Livelock
- **Issue**: Threads respond to each other without making progress.
- **Solution**: Use retry strategies with increasing wait times.

### 4. Thread Starvation
- **Issue**: High-priority threads block lower-priority ones.
- **Solution**: Use fair locks and avoid priority-based threading.

### 5. Context Switching Overhead
- **Issue**: Frequent thread switching degrades performance.
- **Solution**: Minimize thread count and use thread pools.

### 6. Memory Consistency Errors
- **Issue**: Threads see inconsistent shared variable values.
- **Solution**: Use `volatile` or synchronized access.
Volatile solves visibility issue when single thread writes. If multiple thread write then
it's atomicity problem which needs to be solved by synchronized keyword or locking or
atomic types.

### 7. Blocking Operations
- **Issue**: Threads blocked by I/O hold resources too long.
- **Solution**: Use non-blocking I/O or async mechanisms.

### 8. Thread Leaks
- **Issue**: Threads created but not terminated cause resource leaks.
- **Solution**: Shut down thread pools and manage lifecycles.
We should never create threads with new Thread. Instead we should use
ThreadPools which guarantee proper lifecycle, queueing etc.
Just we should always shutdown executors to avoid leaking as well.

### 9. Improper Thread Pool Use
- **Issue**: Too few or too many threads in a pool impacts performance.
- **Solution**: Choose suitable thread pool sizes.
In case of CPU operations (heavy calculations etc.) thread pool size could be equal to
number CPU cores to gradually split load between cores. In case of IO operations (http calls, db call etc)
thread pools size should be calculated with taking into account waiting time, so would be 10 times bigger

### 10. Concurrency Bugs in Data Structures
- **Issue**: Concurrent data structure use causes issues.
- **Solution**: Use thread-safe collections like `ConcurrentHashMap`.

### 11. Fork/Join Pool Misuse
- **Issue**: Task imbalance leads to poor performance.
- **Solution**: Divide tasks evenly and follow Fork/Join patterns.
  Misusing the Fork/Join pool occurs when you execute blocking I/O operations or choose an inefficient task threshold, 
  which leads to thread starvation and excessive context-switching overhead.
