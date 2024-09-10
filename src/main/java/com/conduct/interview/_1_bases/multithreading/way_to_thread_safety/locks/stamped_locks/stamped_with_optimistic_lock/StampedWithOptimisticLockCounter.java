package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.locks.stamped_locks.stamped_with_optimistic_lock;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.IntStream;

@Getter
@Setter
public class StampedWithOptimisticLockCounter {
    private int counter;
    private StampedLock lock = new StampedLock();
    public void incrementCounter() {
//        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
//        Lock writeLock = readWriteLock.writeLock();
        long writeStamp = lock.writeLock();
//        long stamp = lock.tryOptimisticRead();
        try {
//            writeLock.lock();
            counter++;
        } finally {
//            writeLock.unlock();
            lock.unlock(writeStamp);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        StampedWithOptimisticLockCounter reentrantLockCounter = new StampedWithOptimisticLockCounter();

        IntStream.range(0, 1000)
                .forEach(count -> service.submit(reentrantLockCounter::incrementCounter));
        service.awaitTermination(1000, TimeUnit.MILLISECONDS);
        service.shutdown();
        System.out.println(reentrantLockCounter.getCounter());
    }
}
