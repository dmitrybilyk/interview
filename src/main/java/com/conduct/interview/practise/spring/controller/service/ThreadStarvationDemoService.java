package com.conduct.interview.practise.spring.controller.service;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ThreadStarvationDemoService {

    private final AtomicInteger counter = new AtomicInteger();
    private final Executor blockingExecutor;

    private final Set<String> activeThreads = new ConcurrentSkipListSet<>();

    public ThreadStarvationDemoService(Executor blockingExecutor) {
        this.blockingExecutor = blockingExecutor;
    }

    public CompletableFuture<String> runBlockingTaskFixed() {
        int taskId = counter.incrementAndGet();
        return CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Task " + taskId + " started in thread " + threadName);

            try {
                Thread.sleep(30_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Task " + taskId + " finished in thread " + threadName);
            return "Task " + taskId + " handled by thread " + threadName;
        }, blockingExecutor);
    }

    public String runBlockingTask() {
        String threadName = Thread.currentThread().getName();
        int taskId = counter.incrementAndGet();
        activeThreads.add(threadName);

        System.out.println("Task " + taskId + " started in thread " + threadName);

        try {
            // Simulate a long-running blocking task (30s)
            Thread.sleep(30_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Task " + taskId + " finished in thread " + threadName);
        activeThreads.remove(threadName);

        return "starvation";
    }
}
