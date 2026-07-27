package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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