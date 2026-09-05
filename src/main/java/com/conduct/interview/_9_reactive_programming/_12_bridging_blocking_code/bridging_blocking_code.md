# Bridging Blocking Code and External Callbacks

- **`Mono.fromCallable(() -> blockingCall())`** (also `fromRunnable`,
  `fromSupplier`): wraps a plain blocking call as a cold `Mono` - it only
  actually runs when subscribed to. Combine with
  `.subscribeOn(Schedulers.boundedElastic())` so the block doesn't
  happen on an event-loop thread (see
  [_8_schedulers_and_threading](../_8_schedulers_and_threading)).
- **`Mono.defer(() -> ...)`**: goes one step further than
  `fromCallable` - not just the *execution* is deferred until
  subscription, the *creation of the Mono itself* is too. Useful when you
  need to make a decision (e.g. which Mono to return) at subscribe time
  rather than at chain-assembly time, or want a fresh value per
  subscriber instead of one cached at assembly time.
- **`Sinks`**: a programmatic bridge for pushing values in from
  *outside* the reactive world (a callback API, a message listener) -
  the sink is a hot publisher (see [_4_cold_vs_hot](../_4_cold_vs_hot)):
  it runs independently and just broadcasts whatever gets pushed into it
  via `tryEmitNext`/`tryEmitComplete` to whichever subscribers are
  currently attached.
