package com.conduct.interview._1_bases.multithreading.producer_consumer.with_reentrant_lock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class WithReentrantLock {
  public static final String EOF = "EOF";

  public static void main(String[] args) {
    List<String> buffer = new ArrayList<>();

    ReentrantLock bufferLock = new ReentrantLock();

    Thread producerThread = new Thread(new Producer(buffer, bufferLock));
    producerThread.setName("producerThread");

    Thread consumerThread1 = new Thread(new Consumer(buffer, bufferLock));
    consumerThread1.setName("consumerThread1");

    Thread consumerThread2 = new Thread(new Consumer(buffer, bufferLock));
    consumerThread2.setName("consumerThread2");

    producerThread.start();
    consumerThread1.start();
    consumerThread2.start();
  }
}

class Producer implements Runnable {
  private List<String> buffer;
  private ReentrantLock bufferLock;

  public Producer(List<String> buffer, ReentrantLock bufferLock) {
    this.buffer = buffer;
    this.bufferLock = bufferLock;
  }

  @Override
  public void run() {
    String numbers[] = {"1", "2", "3"};
    for (String number : numbers) {
      bufferLock.lock();
      try {
        buffer.add(number);
      } finally {
        bufferLock.unlock();
      }

      try {
        Random random = new Random();
        Thread.sleep(random.nextInt(2000));
      } catch (InterruptedException e) {
        System.out.println(Thread.currentThread().getName() + " interrupted.");
      }
      System.out.println(Thread.currentThread().getName() + " added " + number);
    }
    System.out.println(Thread.currentThread().getName() + " added " + WithReentrantLock.EOF);
    bufferLock.lock();
    try {
      buffer.add(WithReentrantLock.EOF);
    } finally {
      bufferLock.unlock();
    }
  }
}

class Consumer implements Runnable {
  private List<String> buffer;
  private ReentrantLock bufferLock;

  public Consumer(List<String> buffer, ReentrantLock bufferLock) {
    this.buffer = buffer;
    this.bufferLock = bufferLock;
  }

  @Override
  public void run() {

    while (true) {
      bufferLock.lock();
      try {
        if (buffer.isEmpty()) {
          continue;
        }
        if (buffer.get(0).equals(WithReentrantLock.EOF)) {
          System.out.println(Thread.currentThread().getName() + " exiting.");
          break;
        } else {
          System.out.println(Thread.currentThread().getName() + " removed " + buffer.remove(0));
          try {
            Random random = new Random();
            Thread.sleep(random.nextInt(2000));
          } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " interrupted.");
          }
        }
      } finally {
        bufferLock.unlock();
      }
    }
  }
}
