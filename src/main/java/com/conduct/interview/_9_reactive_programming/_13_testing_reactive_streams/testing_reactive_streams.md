# Testing Reactive Streams

You can't just call a getter on a `Mono`/`Flux` to check "what did it
produce" - nothing runs until something subscribes and requests. Reactor
Test's **`StepVerifier`** is a Subscriber built for tests: it subscribes
for you, asserts on the exact sequence of signals it receives
(`expectNext`, `expectError`, `expectComplete`), and can control demand
and virtual time itself.

`Sinks`/`Flux.interval`-based code that would otherwise take real
wall-clock seconds to test can run instantly with
`StepVerifier.withVirtualTime(...)`, which fakes the clock.

The demo lives in `src/test/java/.../_13_testing_reactive_streams/` (not
next to this file) because `reactor-test` is only on the test
classpath - see `ReactiveStreamsTest.java`.
