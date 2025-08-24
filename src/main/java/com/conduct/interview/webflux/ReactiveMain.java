package com.conduct.interview.webflux;

import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ReactiveMain {
    public static void main(String[] args) {
        AtomicInteger counter = new AtomicInteger(0);

        Mono<Integer> fromSupplierMono =
                Mono.fromSupplier(counter::incrementAndGet).cache();

        Mono<Integer> deferMono =
                Mono.defer(() -> Mono.fromSupplier(counter::incrementAndGet)).cache();

        System.out.println("fromSupplier:");
        fromSupplierMono.subscribe(v -> System.out.println("sub1: " + v));
        fromSupplierMono.subscribe(v -> System.out.println("sub2: " + v));

        System.out.println("\ndefer:");
        deferMono.subscribe(v -> System.out.println("sub1: " + v));
        deferMono.subscribe(v -> System.out.println("sub2: " + v));




    }

    public static String getData() {
        System.out.println("Method executed");
        return "result";
    }


}
