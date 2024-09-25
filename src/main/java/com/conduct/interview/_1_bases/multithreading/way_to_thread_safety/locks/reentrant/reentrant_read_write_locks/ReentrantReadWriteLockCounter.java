package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.locks.reentrant.reentrant_read_write_locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReentrantReadWriteLockCounter {
  private int counter;
  private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private Lock readLock = lock.readLock();
  private Lock writeLock = lock.writeLock();

  public void incrementCounter() {
    writeLock.lock();
    try {
      counter++;
    } finally {
      writeLock.unlock();
    }
  }

  public int getCounter() {
    readLock.lock();
    try {
      return counter;
    } finally {
      readLock.unlock();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    ExecutorService service = Executors.newFixedThreadPool(3);
    ReentrantReadWriteLockCounter reentrantLockCounter = new ReentrantReadWriteLockCounter();

    IntStream.range(0, 1000)
        .forEach(count -> service.submit(reentrantLockCounter::incrementCounter));
    service.awaitTermination(1000, TimeUnit.MILLISECONDS);
    service.shutdown();
    System.out.println(reentrantLockCounter.getCounter());
  }
}
