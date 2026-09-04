Lets one or more threads wait until N other things finish. Each worker calls `countDown()` when
done; `await()` blocks until the count reaches 0. One-shot - can't be reset or reused once it
hits zero (that's what `CyclicBarrier` is for).
