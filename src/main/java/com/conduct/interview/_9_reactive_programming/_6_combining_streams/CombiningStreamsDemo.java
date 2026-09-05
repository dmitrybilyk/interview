package com.conduct.interview._9_reactive_programming._6_combining_streams;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class CombiningStreamsDemo {

    public static void main(String[] args) {
        Flux<String> letters = Flux.just("a", "b", "c");
        Flux<Integer> numbers = Flux.just(1, 2, 3);

        Flux.concat(letters, numbers)
                .subscribe(v -> System.out.println("concat -> " + v));

        Flux.merge(
                Flux.interval(Duration.ofMillis(50)).map(i -> "fast-" + i).take(3),
                Flux.interval(Duration.ofMillis(80)).map(i -> "slow-" + i).take(3)
        ).subscribe(v -> System.out.println("merge -> " + v));

        Flux.zip(letters, numbers)
                .subscribe(tuple -> System.out.println("zip -> " + tuple.getT1() + tuple.getT2()));

        sleep(400);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
