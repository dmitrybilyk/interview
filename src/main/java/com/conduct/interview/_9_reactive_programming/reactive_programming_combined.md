# Reactive Programming — Combined Reference

---

## 1. What Is Reactive Programming?

Reactive programming is about processing streams of data (0, 1, or many values, arriving now or later) through a pipeline of transformations, where the **consumer controls how much it receives**. That last part — consumer-driven flow control, aka backpressure — is what separates it from plain callbacks or the classic Observer pattern (which just pushes and can overwhelm a slow listener).

`java.util.concurrent.Flow` (and the identical `org.reactivestreams` library it was based on) defines the contract as four interfaces:

- **Publisher\<T\>** — source of data. Has one method: `subscribe(Subscriber)`.
- **Subscriber\<T\>** — consumer. Gets `onSubscribe`, `onNext`, `onError`, `onComplete`.
- **Subscription** — the live link between one Publisher and one Subscriber. Has `request(n)` (pull n more items) and `cancel()`.
- **Processor\<T,R\>** — both a Subscriber and a Publisher; a stage in the middle of a pipeline (e.g. `map`, `filter` are built out of these).

Reactor's `Mono`/`Flux` and RxJava's types are just implementations of `Publisher` with a fluent operator API layered on top.

---

## 2. Mono vs Flux

Reactor gives you two `Publisher` implementations, distinguished by **cardinality** — how many values they can ever emit:

- **Mono\<T\>** — zero or one value, then `onComplete` (or `onError`). Think of it as a lazy, async `Optional<T>` / `CompletableFuture<T>`.
- **Flux\<T\>** — zero to N values (possibly infinite), then `onComplete` (or `onError`).

Both are just `Publisher<T>` under the hood — same `onSubscribe` / `onNext` / `onError` / `onComplete` contract. The Mono/Flux split exists so the API can encode "at most one" at the type level (e.g. a repository's `findById` naturally returns `Mono<User>`, a `findAll` returns `Flux<User>`).

Nothing runs until `subscribe()` is called — both types are lazy.

---

## 3. How the Protocol Works — Two Directions

A reactive pipeline is **not one-directional**. Two independent signals travel through the same `Subscription`, in opposite directions:

```
   Subscriber                                  Publisher
       |                                            |
       |------------ publisher.subscribe(this) ---->|
       |                                            |
       |<----------- onSubscribe(subscription) -----|   (handshake)
       |                                            |
       |------------ subscription.request(2) ------>|   UPSTREAM: demand
       |                                            |
       |<----------------- onNext(item) ------------|   DOWNSTREAM: data
       |<----------------- onNext(item) ------------|
       |                                            |
       |------------ subscription.request(2) ------>|   UPSTREAM: demand again
       |<----------------- onNext(item) ------------|
       |<----------------- onNext(item) ------------|
       |                                            |
       |<----------------- onComplete() ------------|
```

- **Downstream** (`onNext` / `onError` / `onComplete`): data flows Publisher → Subscriber.
- **Upstream** (`request(n)` / `cancel()`): control flows Subscriber → Publisher. This is what makes it *reactive pull* instead of *push* — the Subscriber sets the pace.

The "drain loop" pattern (used by both Reactor and RxJava) ensures that `request(n)` called from inside `onNext` never causes infinite recursion: it only increments a demand counter and lets the already-running emit loop pick it up.

When you call `flux.subscribe(item -> ...)` with just a lambda, Reactor builds a hidden Subscriber that calls `request(Long.MAX_VALUE)` immediately — "give me everything, I don't want backpressure." That's valid; it just opts out of flow control.

---

## 4. Cold vs Hot Publishers

- **Cold** (default): the sequence is reproduced from scratch for every subscriber. Two subscribers to the same `Flux.fromIterable(...)` each get their own independent run. This is why calling `subscribe()` twice on a "network call" Mono makes two network calls.
- **Hot**: the sequence runs on its own, independent of any subscriber, and broadcasts to whoever is subscribed at that moment. Late subscribers miss earlier values. Reactor's `Sinks` and `ConnectableFlux` (`.publish()`) make things hot.

**Rule of thumb**: cold for "recompute per subscriber" (DB/HTTP calls); hot for "broadcast one live thing to many" (price ticks, UI events, WebSocket messages).

---

## 5. Operators: map vs flatMap

- **`map(T -> R)`**: transforms a value into another plain value, synchronously, in place. 1 item in, 1 item out.
- **`flatMap(T -> Publisher<R>)`**: transforms a value into a new publisher (e.g. another async call) and flattens its results into the outer stream. Needed whenever the transformation itself is async.

If you `map` with a function that returns a `Publisher`, you get `Flux<Publisher<R>>` — a stream of un-subscribed-to publishers, almost never what you want. `flatMap` subscribes to each inner publisher and merges emissions into one flat stream (concurrently, out of order by default).

Use **`concatMap`** instead of `flatMap` when you need to preserve order at the cost of concurrency.

---

## 6. Combining Streams

| Operator | Behaviour |
|---|---|
| `concat` | Subscribes to sources one after another — second doesn't start until first completes. Ordered, sequential. |
| `merge` | Subscribes to all sources immediately, interleaves whatever arrives. No ordering guarantee. |
| `zip` | Pairs the *n*-th item of each source into a tuple. Waits for the slowest per pair. Completes when the shortest source completes. |

---

## 7. Backpressure

Backpressure is what `request(n)` gives you for free: a subscriber that only asks for what it can handle can never be flooded by a fast producer.

The hard case is a **true source of unbounded push** that can't be told to slow down (e.g. `Flux.interval`, an incoming WebSocket, a hardware sensor). Pick an overflow strategy:

| Operator | Behaviour |
|---|---|
| `onBackpressureBuffer()` | Queue items up. Risk: unbounded memory. |
| `onBackpressureDrop()` | Discard new items while subscriber is busy. |
| `onBackpressureLatest()` | Keep only the most recent item, drop the rest. |
| `limitRate(n)` | Request upstream in capped chunks of `n`, refilling as consumed — smooths the pipeline without dropping. |

---

## 8. Schedulers and Threading — publishOn vs subscribeOn

Both switch which thread operators run on, but at different points in the chain:

- **`subscribeOn(scheduler)`**: affects where the *subscription itself* happens — i.e. where the source starts emitting. Placement in the chain doesn't matter; only the **first** `subscribeOn` in a chain has effect.
- **`publishOn(scheduler)`**: affects where everything **after it** in the chain runs. You can have several, each switching the thread for the rest of the pipeline.

**Rule of thumb**: `subscribeOn` picks the thread for the source (e.g. "run this blocking JDBC call on `boundedElastic`"); `publishOn` picks the thread for a specific downstream segment.

**Built-in schedulers**:
- `Schedulers.parallel()` — fixed thread pool (CPU cores), for CPU-bound work.
- `Schedulers.boundedElastic()` — elastic pool capped at 10× cores, for blocking I/O.
- `Schedulers.single()` — single reusable thread.
- `Schedulers.immediate()` — the calling thread (no switch).

---

## 9. Error Handling

An error is a **terminal signal** — once `onError` fires, the sequence is over, no more `onNext` will come. Options:

| Operator | Behaviour |
|---|---|
| `onErrorReturn(fallback)` | Replace the error with one fallback value, then complete. |
| `onErrorResume(t -> Publisher)` | Replace the error with an entirely different publisher to continue from (e.g. fall back to a cache). |
| `onErrorContinue((t, item) -> ...)` | Skip just the one failing element, keep the sequence alive. Needs explicit operator support upstream; prefer `onErrorResume` inside `flatMap` when possible. |
| `retry(n)` / `retryWhen(spec)` | Resubscribe to the source from the beginning on error, up to `n` times or per a backoff spec. |

---

## 10. Side Effects and Debugging

These operators observe what flows through without changing it:

| Operator | Observes |
|---|---|
| `doOnNext` / `doOnComplete` / `doOnError` | Each data/terminal signal |
| `doOnSubscribe` / `doOnCancel` / `doOnRequest` | Control signals (subscribe, cancel, request(n)) |
| `doOnEach(signal -> ...)` | Every kind of signal in one callback |
| `.log()` | Shortcut: wires `doOnEach` to print every signal with a configurable category/level |
| `.checkpoint("name")` | Doesn't log by default, but enriches stack traces on error with the checkpoint's name — fastest way to find which operator in a long chain produced a given error |

---

## 11. Context Propagation

Thread-locals don't survive a `publishOn`/`subscribeOn` switch. Reactor has its own immutable, per-subscription key/value store called `Context` for carrying values (e.g. a trace ID) through an async chain.

Two rules:

- **`contextWrite(ctx -> ctx.put(...))`** makes values visible to operators **upstream** of it — context flows from subscriber to publisher (opposite to data). Put `contextWrite` near the **end** of the chain so everything above it can read the value.
- **`Mono.deferContextual(ctx -> ...)`** (or `.transformDeferredContextual`) is how an operator *reads* the current context. There's no static "get context" method — context is scoped to one subscription.

---

## 12. Bridging Blocking Code and External Callbacks

| Tool | Use case |
|---|---|
| `Mono.fromCallable(() -> blockingCall())` | Wrap a blocking call as a cold Mono. Combine with `.subscribeOn(Schedulers.boundedElastic())` so the block doesn't run on an event-loop thread. |
| `Mono.defer(() -> ...)` | Defers both execution AND Mono creation to subscribe time. Use when the decision of *which* Mono to return must happen per-subscriber, not at assembly time. |
| `Sinks` | Push values in from outside the reactive world (a callback API, a message listener). The sink is a hot publisher — it runs independently and broadcasts to current subscribers via `tryEmitNext` / `tryEmitComplete`. |

---

## 13. Testing with StepVerifier

You can't just call a getter on a `Mono`/`Flux` — nothing runs until something subscribes and requests. Reactor Test's **`StepVerifier`** is a Subscriber built for tests:

```java
StepVerifier.create(flux)
    .expectNext("a", "b")
    .expectComplete()
    .verify();
```

- Subscribes for you and asserts the exact sequence of signals (`expectNext`, `expectError`, `expectComplete`).
- Controls demand and virtual time itself.
- `StepVerifier.withVirtualTime(...)` fakes the clock — code based on `Flux.interval` or `Sinks` that would take real seconds runs instantly.

---

## Interview Points

- **"Nothing runs until subscribe"** — Mono/Flux are lazy. Assembling a chain is just building a description; execution starts with the first `subscribe()`.
- **Backpressure = `request(n)`** — downstream controls pace. Without it, a slow consumer gets flooded. `limitRate(n)` is the practical knob most apps actually use.
- **`subscribeOn` vs `publishOn`**: subscribeOn affects the source (where it starts); publishOn affects everything downstream of the call site. Common mistake: putting `subscribeOn` at the bottom and expecting it to switch only the source.
- **Cold vs Hot**: double-subscribing a cold DB Flux = two queries. Hot (Sinks, ConnectableFlux) = one shared stream, late subscribers miss past items.
- **`flatMap` = concurrent async fan-out; `concatMap` = sequential**. Mixing them up is a very common bug — `flatMap` doesn't preserve order.
- **`onErrorContinue` is tricky**: it requires operator cooperation upstream to work correctly. In most cases `flatMap(x -> call(x).onErrorResume(...))` is safer.
- **Context flows upstream** (opposite to data). `contextWrite` at the end of the chain, `deferContextual` to read it.
- **`checkpoint("name")`** is the fastest debug tool for long chains — enriches error stack traces with the assembly location.
- **StepVerifier + withVirtualTime** for testing time-based streams without sleeping in tests.
