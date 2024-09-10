package com.conduct.interview._1_bases.multithreading.java_concurrent_package.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SemaphoreTry {
    public static void main(String[] args) throws InterruptedException {
        SomeCounter someCounter = new SomeCounter();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        IntStream.range(0, 10)
                .forEach(count -> {
//                    someCounter.occupySlot();
                    executorService.execute(someCounter::incrementCounter);
//                    someCounter.releaseSlot();
                });
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        executorService.shutdown();
        System.out.println(someCounter.getCounter());
    }
}

class SomeCounter {
    private int counter;
    private Semaphore semaphore = new Semaphore(5);
    public int getCounter() {
        return counter;
    }

    public void occupySlot() {
        if (semaphore.tryAcquire()) {
            System.out.println(Thread.currentThread().getName() + " occupied");
        } else {
            System.out.println(Thread.currentThread().getName() + " Can't occupy");
            System.out.println("Available slots - " + semaphore.availablePermits());
        }
    }

    public void releaseSlot() {
        semaphore.release();
    }

    public void incrementCounter() {
        occupySlot();
        System.out.println(Thread.currentThread().getName());
        this.counter++;
        releaseSlot();
    }
}
