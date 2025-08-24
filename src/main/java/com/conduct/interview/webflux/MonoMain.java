package com.conduct.interview.webflux;

import reactor.core.publisher.Mono;

public class MonoMain {
    public static void main(String[] args) {
        Mono<String> mono = Mono.just("Result");
        mono.subscribe(
                System.out::println,
                System.out::println,
                () -> System.out.println(" some job is completed "),
                subscription -> subscription.request(1)
        );
    }
}
