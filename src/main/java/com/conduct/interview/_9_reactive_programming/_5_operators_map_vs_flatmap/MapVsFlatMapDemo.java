package com.conduct.interview._9_reactive_programming._5_operators_map_vs_flatmap;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class MapVsFlatMapDemo {

    public static void main(String[] args) {
        // map: synchronous, 1-to-1, no unwrapping needed.
        Flux.just("a", "b", "c")
                .map(String::toUpperCase)
                .subscribe(v -> System.out.println("map -> " + v));

        // flatMap: each value produces its own async Mono, results are
        // flattened back into one Flux<String>. Order is not guaranteed
        // because each fetchUser() call runs independently.
        Flux.just(1, 2, 3)
                .flatMap(MapVsFlatMapDemo::fetchUserName)
                .subscribe(v -> System.out.println("flatMap -> " + v));

        // What NOT to do: map with a function returning a Publisher
        // gives you Flux<Mono<String>> - nothing ever gets subscribed to.
        Flux<Mono<String>> nested = Flux.just(1, 2, 3)
                .map(MapVsFlatMapDemo::fetchUserName);
        System.out.println("map+Publisher produces: " + nested.getClass().getSimpleName()
                + "<Mono<String>> - inner Monos are never subscribed to");

        // concatMap: same idea as flatMap, but preserves order by
        // waiting for one inner publisher to finish before starting the next.
        Flux.just(3, 1, 2)
                .concatMap(id -> fetchUserName(id).delayElement(Duration.ofMillis(id * 10L)))
                .subscribe(v -> System.out.println("concatMap (ordered) -> " + v));

        sleep(200);
    }

    private static Mono<String> fetchUserName(int id) {
        return Mono.just("user-" + id); // stand-in for a real async call (HTTP, DB, ...)
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
