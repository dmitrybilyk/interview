package com.conduct.interview._9_reactive_programming._4_cold_vs_hot;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdVsHotDemo {

    public static void main(String[] args) throws InterruptedException {
        // COLD: each subscriber restarts the counter from 0.
        Flux<Long> cold = Flux.range(0, 3).map(Long::valueOf);
        cold.subscribe(v -> System.out.println("cold subscriber A -> " + v));
        cold.subscribe(v -> System.out.println("cold subscriber B -> " + v));

        // HOT: subscribers share one running sequence; a late subscriber
        // only sees what's emitted after it joins.
        ConnectableFlux<Long> hot = Flux.interval(Duration.ofMillis(100)).publish();
        hot.subscribe(v -> System.out.println("hot subscriber A -> " + v));
        hot.connect(); // starts the ticking now, regardless of subscribers

        Thread.sleep(250);
        hot.subscribe(v -> System.out.println("hot subscriber B (joined late) -> " + v));

        Thread.sleep(300);
    }
}
