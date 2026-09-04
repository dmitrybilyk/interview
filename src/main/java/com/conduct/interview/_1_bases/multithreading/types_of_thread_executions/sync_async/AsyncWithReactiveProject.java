package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async;

import reactor.core.publisher.Mono;

/**
 * Reactive style (Project Reactor): nothing runs until subscribe() is called - the pipeline
 * is just a description of work until then. subscribe() itself doesn't block the caller.
 */
public class AsyncWithReactiveProject {

    public static void main(String[] args) throws InterruptedException {
        Mono.fromCallable(() -> {
                    Thread.sleep(1000);
                    return "Reactive result";
                })
                .subscribe(result -> System.out.println(result));

        System.out.println("Main thread continues...");
        Thread.sleep(2000); // just so the program doesn't exit before the reactive pipeline finishes
    }
}