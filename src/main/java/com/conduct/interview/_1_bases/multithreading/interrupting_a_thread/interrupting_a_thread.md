`interrupt()` does NOT stop a thread. It just flips a flag. What happens next depends on what
the thread is doing right now:

- **sleeping/waiting** (`sleep`, `wait`, `join`, `future.get()`) -> wakes up immediately with
  `InterruptedException`. Easy case.
- **doing a plain blocking HTTP/socket call** -> interrupt does NOTHING. Thread stays stuck until
  the response comes back, someone closes the connection, or a timeout you configured kicks in.
- **running normal code** -> nothing happens either, code must check `isInterrupted()` itself.

Takeaway: `interrupt()` only works if the thing you're waiting on was built to listen for it.
Plain HTTP calls aren't - that's why you always set a timeout, so a stuck thread can't hang forever.
