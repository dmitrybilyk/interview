package com.conduct.interview._9_reactive_programming._7_backpressure;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class BackpressureDemo {

    public static void main(String[] args) {
        // Producer ticks every 10ms, consumer takes 100ms per item -
        // without a strategy this would build an unbounded backlog.
        Flux.interval(Duration.ofMillis(10))
                .onBackpressureDrop(dropped -> System.out.println("dropped -> " + dropped))
                .concatMap(v -> Flux.just(v).delayElements(Duration.ofMillis(100)))
                .take(5)
                .doOnNext(v -> System.out.println("processed -> " + v))
                .blockLast();

        // limitRate: instead of requesting everything at once, pulls in
        // capped batches - useful for paging through a large/expensive source.
        Flux.range(1, 20)
                .limitRate(5)
                .doOnRequest(n -> System.out.println("upstream request(" + n + ")"))
                .subscribe(v -> System.out.println("limitRate -> " + v));
    }
}
