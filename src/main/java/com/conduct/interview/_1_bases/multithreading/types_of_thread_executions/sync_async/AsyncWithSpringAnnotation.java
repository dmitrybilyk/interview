package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Spring runs @Async methods on a separate thread via a proxy - needs @EnableAsync and a
 * Spring context, so there's no plain main() here. Gotcha: calling asyncMethod() from another
 * method of the SAME bean bypasses the proxy and runs synchronously - only calls from a
 * different bean actually go async.
 */
@Service
public class AsyncWithSpringAnnotation {

    @Async
    public void asyncMethod() {
        try {
            Thread.sleep(1000);
            System.out.println("Async method executed");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}