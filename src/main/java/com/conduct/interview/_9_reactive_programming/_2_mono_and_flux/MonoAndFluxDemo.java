package com.conduct.interview._9_reactive_programming._2_mono_and_flux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoAndFluxDemo {

    public static void main(String[] args) {
        Mono<String> mono = Mono.just("single result");
        mono.subscribe(value -> System.out.println("Mono -> " + value));

        Flux<String> flux = Flux.just("a", "b", "c");
        flux.subscribe(value -> System.out.println("Flux -> " + value));

        // A Mono is what you get when you collapse a Flux down to one item
        Flux.just(1, 2, 3)
                .collectList()          // Flux<Integer> -> Mono<List<Integer>>
                .subscribe(list -> System.out.println("Flux.collectList() -> " + list));

        // An empty Mono/Flux still completes - it just skips onNext entirely
        Mono.empty().subscribe(
                v -> System.out.println("never called"),
                e -> System.out.println("never called either"),
                () -> System.out.println("Mono.empty() -> onComplete with no value")
        );
    }
}
