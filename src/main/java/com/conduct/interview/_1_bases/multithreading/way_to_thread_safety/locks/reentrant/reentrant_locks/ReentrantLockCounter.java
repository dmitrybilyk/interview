package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.locks.reentrant.reentrant_locks;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

@Getter
@Setter
public class ReentrantLockCounter {
    private int counter;
    private ReentrantLock lock = new ReentrantLock(true);
    public void incrementCounter() {
        lock.lock();
        try {
            counter++;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        ReentrantLockCounter reentrantLockCounter = new ReentrantLockCounter();

        IntStream.range(0, 1000)
                .forEach(count -> service.submit(reentrantLockCounter::incrementCounter));
        service.awaitTermination(1000, TimeUnit.MILLISECONDS);
        service.shutdown();
        System.out.println(reentrantLockCounter.getCounter());
    }
}
