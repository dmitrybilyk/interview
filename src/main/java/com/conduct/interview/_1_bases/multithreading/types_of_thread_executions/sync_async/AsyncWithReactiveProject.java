package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async;

import reactor.core.publisher.Mono;

public class AsyncWithReactiveProject {

    public static void main(String[] args) throws InterruptedException {

        Mono.fromCallable(() -> {
            Thread.sleep(1000);
            return "Reactive result";
        })
        .subscribe(result -> System.out.println(result));

        System.out.println("Main thread continues...");
        Thread.sleep(2000);
    }
}