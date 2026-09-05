package com.conduct.interview._9_reactive_programming._8_schedulers_and_threading;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class SchedulersDemo {

    public static void main(String[] args) {
        Flux.range(1, 2)
                .doOnNext(v -> log("source emits " + v))
                .publishOn(Schedulers.parallel())
                .doOnNext(v -> log("after publishOn, processing " + v))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(v -> log("this still runs on parallel() - publishOn already switched it"))
                .blockLast();

        // subscribeOn placement doesn't matter - it always affects the source.
        Flux.range(1, 2)
                .doOnNext(v -> log("subscribeOn alone -> source emits " + v))
                .subscribeOn(Schedulers.boundedElastic())
                .blockLast();
    }

    private static void log(String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + message);
    }
}
