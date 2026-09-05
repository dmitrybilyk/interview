# Reactive Programming

Index of this topic. Each numbered folder covers one aspect, with a short
`*.md` explanation and a small runnable demo next to it.

1. [_1_reactive_streams_basics](_1_reactive_streams_basics) - what "reactive" means, the Reactive Streams spec, why it exists
2. [_2_mono_and_flux](_2_mono_and_flux) - Reactor's two Publisher types: 0..1 vs 0..N
3. [_3_manual_publisher_subscriber](_3_manual_publisher_subscriber) - build a Publisher/Subscriber/Subscription by hand, step by step
4. [_4_cold_vs_hot](_4_cold_vs_hot) - per-subscriber replay vs a shared, already-running stream
5. [_5_operators_map_vs_flatmap](_5_operators_map_vs_flatmap) - transforming values vs transforming into publishers
6. [_6_combining_streams](_6_combining_streams) - zip, merge, concat
7. [_7_backpressure](_7_backpressure) - what happens when a producer is faster than its consumer
8. [_8_schedulers_and_threading](_8_schedulers_and_threading) - publishOn vs subscribeOn
9. [_9_error_handling](_9_error_handling) - onErrorReturn / onErrorResume / onErrorContinue / retry
10. [_10_side_effects_and_debugging](_10_side_effects_and_debugging) - doOnNext, log(), checkpoint() and friends
11. [_11_context_propagation](_11_context_propagation) - Reactor Context, contextWrite / deferContextual
12. [_12_bridging_blocking_code](_12_bridging_blocking_code) - Mono.fromCallable, Mono.defer, Sinks
13. [_13_testing_reactive_streams](_13_testing_reactive_streams) - StepVerifier

Start with 1-3 in order - everything after that is a variation on the same
publish/subscribe/request loop introduced in item 3.
