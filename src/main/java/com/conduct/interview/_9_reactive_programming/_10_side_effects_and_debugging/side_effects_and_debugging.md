# Side Effects and Debugging

These operators don't change the values in the stream - they just let
you observe (or peek into) what's flowing through, without affecting it:

- **`doOnNext` / `doOnComplete` / `doOnError`**: run a side-effecting
  callback on each signal, then pass it through unchanged.
- **`doOnSubscribe` / `doOnCancel` / `doOnRequest`**: observe the
  *control* signals from [_3_manual_publisher_subscriber](../_3_manual_publisher_subscriber)
  (when someone subscribes, cancels, or calls `request(n)`).
- **`doOnEach(signal -> ...)`**: one callback for every kind of signal
  (`onNext`, `onError`, `onComplete`), more flexible than the individual
  `doOn*` methods when you need to handle them uniformly.
- **`.log()`**: shortcut that wires up `doOnEach` to print every signal;
  configurable with a category and log level.
- **`.checkpoint("name")`**: doesn't log anything by default, but if an
  error later occurs it enriches the stack trace with the checkpoint's
  name and location - the fastest way to find *which operator in a long
  chain* produced a given error.
