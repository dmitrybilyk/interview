# Mono vs Flux

Reactor gives you two `Publisher` implementations, distinguished by
**cardinality** - how many values they can ever emit:

- **Mono\<T\>** - zero or one value, then `onComplete` (or `onError`).
  Think of it as a lazy, async `Optional<T>` / `CompletableFuture<T>`.
- **Flux\<T\>** - zero to N values (possibly infinite), then `onComplete`
  (or `onError`).

Both are just `Publisher<T>` under the hood - same `onSubscribe` /
`onNext` / `onError` / `onComplete` contract from
[_1_reactive_streams_basics](../_1_reactive_streams_basics). The
Mono/Flux split exists purely so the API can encode "at most one" at the
type level (e.g. a repository's `findById` naturally returns `Mono<User>`,
a `findAll` naturally returns `Flux<User>`).

Nothing runs until `subscribe()` is called - both types are lazy
(see [_4_cold_vs_hot](../_4_cold_vs_hot)).
