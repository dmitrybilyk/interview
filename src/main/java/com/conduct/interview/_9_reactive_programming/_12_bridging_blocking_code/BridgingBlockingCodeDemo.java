package com.conduct.interview._9_reactive_programming._12_bridging_blocking_code;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

public class BridgingBlockingCodeDemo {

    public static void main(String[] args) throws InterruptedException {
        Mono.fromCallable(BridgingBlockingCodeDemo::slowBlockingCall)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(v -> System.out.println("fromCallable -> " + v));

        // defer: the supplier runs again for every subscriber - each gets a fresh value.
        Mono<Long> deferred = Mono.defer(() -> Mono.just(System.nanoTime()));
        deferred.subscribe(v -> System.out.println("defer subscriber 1 -> " + v));
        deferred.subscribe(v -> System.out.println("defer subscriber 2 -> " + v));

        // Sinks: a hot bridge - values pushed in from a callback-style API
        // are broadcast to whoever is subscribed right now.
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        sink.asFlux().subscribe(v -> System.out.println("sink subscriber -> " + v));

        externalCallbackApi(sink::tryEmitNext);

        Thread.sleep(50);
        sink.tryEmitComplete();
    }

    private static String slowBlockingCall() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "blocking result on " + Thread.currentThread().getName();
    }

    /** Stand-in for a third-party SDK that only knows how to call back with values. */
    private static void externalCallbackApi(java.util.function.Consumer<String> onEvent) {
        onEvent.accept("event-1");
        onEvent.accept("event-2");
    }
}
