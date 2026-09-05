# Cold vs Hot Publishers

- **Cold** (the default): the sequence is (re)produced from scratch for
  every subscriber. Two subscribers to the same `Flux.fromIterable(...)`
  each get their own independent run, from the start. This is why calling
  `flux.subscribe()` twice on a "network call" Mono makes two network
  calls - nothing happened until each subscribe.
- **Hot**: the sequence runs on its own, independent of any subscriber,
  and just broadcasts whatever it produces to whoever happens to be
  subscribed *at that moment*. A late subscriber can miss earlier values.
  Reactor's `Sinks` (see [_12_bridging_blocking_code](../_12_bridging_blocking_code))
  and `ConnectableFlux` (`.publish()`) are how you make something hot.

Rule of thumb: use cold for "recompute per subscriber" (DB calls, HTTP
calls); use hot for "broadcast one live thing to many listeners"
(price ticks, UI events, WebSocket messages).
