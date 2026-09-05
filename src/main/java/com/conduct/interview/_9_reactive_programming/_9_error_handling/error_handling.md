# Error Handling

An error is a **terminal signal** - once `onError` fires, the sequence is
over, no more `onNext` will come (unlike a checked exception you can
catch and keep going past). The operators below give you different ways
to react to that:

- **`onErrorReturn(fallback)`**: replace the error with one fallback
  value, then complete. The whole sequence still ends there.
- **`onErrorResume(t -> Publisher)`**: replace the error with an entirely
  different publisher to continue from (e.g. fall back to a cache).
- **`onErrorContinue((t, item) -> ...)`**: skip just the one failing
  element and keep the sequence alive for the rest - the only one of
  these that doesn't terminate the stream. Needs explicit operator
  support upstream to work correctly; prefer `onErrorResume` inside a
  `flatMap`/`map` when possible.
- **`retry(n)`** / **`retryWhen(spec)`**: resubscribe to the source (from
  the beginning) on error, up to `n` times or per a backoff spec.
