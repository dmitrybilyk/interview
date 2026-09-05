package com.conduct.interview._9_reactive_programming._10_side_effects_and_debugging;

import reactor.core.publisher.Flux;

public class SideEffectsAndDebuggingDemo {

    public static void main(String[] args) {
        Flux.just(1, 2, 3)
                .doOnSubscribe(s -> System.out.println("doOnSubscribe"))
                .doOnRequest(n -> System.out.println("doOnRequest(" + n + ")"))
                .doOnNext(v -> System.out.println("doOnNext(" + v + ")"))
                .doOnComplete(() -> System.out.println("doOnComplete"))
                .subscribe();

        Flux.just(1, 2, 3)
                .doOnEach(signal -> System.out.println("doOnEach -> " + signal))
                .subscribe();

        Flux.just(1, 2, 0)
                .map(i -> 10 / i)
                .checkpoint("dividing by i")
                .onErrorResume(e -> {
                    System.out.println("error came from a checkpoint-annotated step: " + e);
                    return Flux.empty();
                })
                .subscribe();
    }
}
