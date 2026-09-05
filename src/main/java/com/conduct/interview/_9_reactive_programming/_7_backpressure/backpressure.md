# Backpressure

Backpressure is what `request(n)` gives you for free (see
[_3_manual_publisher_subscriber](../_3_manual_publisher_subscriber)): a
subscriber that only asks for what it can handle can never be flooded by
a fast producer, because the producer is contractually not allowed to
send more than was requested.

The hard case is a **true source of unbounded push** that can't be told
to slow down (e.g. `Flux.interval`, an incoming websocket, a hardware
sensor). There, you pick an overflow strategy for what to do with items
that arrive before they're requested:

- `onBackpressureBuffer()` - queue them up (risk: unbounded memory).
- `onBackpressureDrop()` - discard new items while the subscriber is busy.
- `onBackpressureLatest()` - keep only the most recent item, drop the rest.
- `limitRate(n)` - a lighter tool: rather than requesting `Long.MAX_VALUE`
  upstream, request in capped chunks of `n`, refilling as they're
  consumed - smooths out the pipeline without any dropping.
