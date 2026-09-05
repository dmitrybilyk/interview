# Reactive Streams Basics

Reactive programming is about processing streams of data (0, 1, or many
values, arriving now or later) through a pipeline of transformations,
where the **consumer controls how much it receives**. That last part -
consumer-driven flow control, aka backpressure - is what separates it
from plain callbacks or the classic Observer pattern (which just pushes
and can overwhelm a slow listener).

`java.util.concurrent.Flow` (and the identical `org.reactivestreams`
library it was based on) defines the contract as four interfaces:

- **Publisher\<T\>** - source of data. Has one method: `subscribe(Subscriber)`.
- **Subscriber\<T\>** - consumer. Gets `onSubscribe`, `onNext`, `onError`, `onComplete`.
- **Subscription** - the live link between one Publisher and one Subscriber.
  Has `request(n)` (pull `n` more items) and `cancel()`.
- **Processor\<T,R\>** - both a Subscriber and a Publisher; a stage in the
  middle of a pipeline (e.g. `map`, `filter` are built out of these).

Reactor's `Mono`/`Flux` (see [_2_mono_and_flux](../_2_mono_and_flux)) and
RxJava's types are just implementations of `Publisher` with a fluent
operator API layered on top.

Run `ReactiveStreamsBasicsDemo` to see the four interfaces in action using
plain Reactor types, then go to
[_3_manual_publisher_subscriber](../_3_manual_publisher_subscriber) to
implement them yourself and see exactly how `request(n)` and `onNext`
travel in opposite directions through the pipeline.
