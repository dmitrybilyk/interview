package com.conduct.interview.practise.spring.controller.multithreading.service.future.check;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CompletableFutureParallelDemo {

    private static final int TASK_COUNT = 8;

    public static void main(String[] args) {
        System.out.println("Available CPUs: " + Runtime.getRuntime().availableProcessors());
        System.out.println();

        // Simulate a list of "files" or "jobs"
        List<String> files = new ArrayList<>();
        for (int i = 1; i <= TASK_COUNT; i++) {
            files.add("File-" + i);
        }

        // Sequential version
        long startSeq = System.currentTimeMillis();
        List<String> seqResults = files.stream()
                .map(CompletableFutureParallelDemo::processFile)
                .collect(Collectors.toList());
        long durationSeq = System.currentTimeMillis() - startSeq;

        // Parallel version using CompletableFuture
        long startPar = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(
                Math.min(TASK_COUNT, Runtime.getRuntime().availableProcessors())
        );

        List<CompletableFuture<String>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> processFile(file), executor))
                .collect(Collectors.toList());

        // Wait for all tasks to finish
        List<String> parResults = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        long durationPar = System.currentTimeMillis() - startPar;

        executor.shutdown();

        System.out.println("\n✅ Sequential took: " + durationSeq + " ms");
        System.out.println("✅ Parallel   took: " + durationPar + " ms");
        System.out.println("\nResults: " + parResults);
    }

    // Simulated expensive operation (e.g., parsing, resizing, etc.)
    private static String processFile(String name) {
        try {
            System.out.println(Thread.currentThread().getName() + " processing " + name);
            Thread.sleep(1000); // Simulate heavy work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return name + " ✅ processed";
    }
}
