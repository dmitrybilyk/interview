package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * How exceptions from a task actually surface, depending on how you run it.
 */
public class FutureExceptionDemo {

    public static void main(String[] args) throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {

            // 1. execute() with a throwing Runnable: the exception goes to the thread's
            //    UncaughtExceptionHandler (default: printed to stderr) - caller never sees it at all.
            executor.execute(() -> {
                throw new RuntimeException("Boom from execute()");
            });

            // 2. submit() with a throwing task: the exception is CAPTURED in the Future,
            //    not thrown anywhere - it only surfaces when you call get().
            Future<Integer> future = executor.submit(() -> {
                throw new IllegalStateException("Boom from submit()");
            });

            // 3. If you never call get(), the exception above is silently swallowed - no log,
            //    no crash, nothing. This is the actual gotcha, not just "exceptions are wrapped".
            System.out.println("Task submitted, exception not observed yet...");

            Thread.sleep(500); // let case 1's stack trace print first, just for readable output

            // 4. Calling get() is what surfaces it - wrapped in ExecutionException,
            //    original exception available via getCause().
            try {
                future.get();
            } catch (java.util.concurrent.ExecutionException e) {
                System.out.println("Caught via get(): " + e.getCause());
            }
        }
    }
}
