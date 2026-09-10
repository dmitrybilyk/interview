package com.conduct.interview._9_reactive_programming._4_cold_vs_hot;

import reactor.core.publisher.Flux;

public class ColdCheck {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.just(3, 5, 6, 7);
        flux.subscribe(integer -> System.out.println("processed in 1 - " + integer));
        flux.subscribe(integer -> System.out.println("processed in 2 - " + integer));
    }
}
