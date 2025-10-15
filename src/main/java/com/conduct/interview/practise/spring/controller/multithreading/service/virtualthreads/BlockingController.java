package com.conduct.interview.practise.spring.controller.multithreading.service.virtualthreads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/api")
class BlockingController {

    @Autowired
    private Executor virtualThreadExecutor;

    @GetMapping("/work")
    public String doWork(@RequestParam(defaultValue = "1000") long delayMillis) throws InterruptedException {
        // Simulate blocking I/O or computation
        Thread.sleep(delayMillis);
        return "Done at " + Instant.now();
    }

    @GetMapping("/vt/work")
    public CompletableFuture<String> doVirtualWork(@RequestParam(defaultValue = "1000") long delayMillis) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Done (VT) at " + Instant.now();
        }, virtualThreadExecutor);
    }

}