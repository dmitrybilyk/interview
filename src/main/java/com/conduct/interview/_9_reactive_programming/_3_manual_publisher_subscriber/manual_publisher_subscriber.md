# Manual Publisher / Subscriber (build it yourself)

Goal: implement the four Reactive Streams roles from
[_1_reactive_streams_basics](../_1_reactive_streams_basics) by hand, with
no Reactor/RxJava operators involved, so the direction data and control
flow in a reactive pipeline stops being magic.

Files, in the order you should read them:

1. `SimplePublisher.java` - holds a list of items. `subscribe()` just
   creates a `Subscription` and hands it to the subscriber. It does
   nothing else - **a Publisher on its own never pushes anything**.
2. `SimpleSubscription.java` - the actual engine. It only emits an item
   when it has been given permission to (`request(n)`), and never more
   than it was given permission for.
3. `LoggingSubscriber.java` - asks for a small batch of items at a time
   (not "give me everything"), so you can see the pipeline pause between
   batches.
4. `ManualPublisherSubscriberDemo.java` - wires the three together and
   prints every call in order.

## The two directions

A reactive pipeline is not one-directional. Two independent signals
travel through the same `Subscription`, in opposite directions:

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
       |<----------------- onNext(item) ------------|   DOWNSTREAM: data again
       |<----------------- onNext(item) ------------|
       |                                            |
       |<----------------- onComplete() ------------|
```

- **Downstream** (`onNext` / `onError` / `onComplete`): data flows from
  the Publisher to the Subscriber. This is the only direction a plain
  Observer pattern has.
- **Upstream** (`request(n)` / `cancel()`): control flows from the
  Subscriber back to the Publisher, through the `Subscription`. This is
  what makes it *reactive pull* instead of *push* - the Subscriber sets
  the pace, so a slow consumer can never be flooded (see
  [_7_backpressure](../_7_backpressure)).

Run `ManualPublisherSubscriberDemo` and read the console output top to
bottom - you'll see exactly this ping-pong: `request(3)`, three `onNext`
calls, `request(3)` again, and so on until `onComplete`.

## The one subtlety: request() can be called back from inside onNext()

`LoggingSubscriber.onNext()` calls `subscription.request(n)` again once
it has consumed a batch - and that call happens synchronously, *from
inside* the very `onNext` call that the `Subscription`'s emit-loop is in
the middle of making. Naively, that's an infinite recursive call stack:
`request -> emit loop -> onNext -> request -> emit loop -> onNext -> ...`.

`SimpleSubscription` avoids this with the "drain loop" pattern that real
implementations (Reactor, RxJava) use: `request(n)` never emits directly.
It just adds `n` to an outstanding-demand counter and tries to become the
one thread actually emitting (tracked by `wip`, "work in progress"). If
someone is already emitting, the new demand is simply added to the
counter and picked up by the loop that's already running - no re-entrant
call, no growing stack, regardless of how many items there are.

## Bonus: what Reactor's Flux does internally

`flux.subscribe(item -> ...)` (a single lambda, no explicit `Subscriber`)
builds a hidden internal `Subscriber` that calls
`request(Long.MAX_VALUE)` immediately in `onSubscribe` - i.e. "just give
me everything, I don't want backpressure". See
`flux_internal_subscribe.puml` for the sequence diagram. Compare that
single, unbounded `request` with `LoggingSubscriber`'s repeated small
`request(n)` calls above - both are 100% valid Reactive Streams
subscribers, they just choose a different point on the
push-vs-pull spectrum.
