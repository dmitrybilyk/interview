# map vs flatMap

- **`map(T -> R)`**: transforms a value into another plain value,
  synchronously, in place. 1 item in, 1 item out.
- **`flatMap(T -> Publisher<R>)`**: transforms a value into a *new
  publisher* (e.g. another async call) and flattens its results into the
  outer stream. Needed whenever the transformation itself is async.

If you `map` a `Flux<T>` with a function that returns a `Publisher`, you
get a `Flux<Publisher<R>>` - a stream of un-subscribed-to publishers,
which is almost never what you want. `flatMap` subscribes to each inner
publisher for you and merges their emissions into one flat stream (by
default, concurrently and out of order - use `concatMap` if you need to
preserve order at the cost of concurrency).
