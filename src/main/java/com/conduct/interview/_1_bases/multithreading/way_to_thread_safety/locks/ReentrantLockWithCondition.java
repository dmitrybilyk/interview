package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.locks;

import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockWithCondition {

  private Stack<String> stack = new Stack<>();
  private static final int CAPACITY = 5;

  private ReentrantLock lock = new ReentrantLock();
  private Condition stackEmptyCondition = lock.newCondition();
  private Condition stackFullCondition = lock.newCondition();

  private void pushToStack(String item) throws InterruptedException {
    try {
      lock.lock();
      if (stack.size() == CAPACITY) {
        System.out.println(Thread.currentThread().getName() + " wait on stack full");
        stackFullCondition.await();
      }
      System.out.println("Pushing the item " + item);
      stack.push(item);
      stackEmptyCondition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  private String popFromStack() throws InterruptedException {
    try {
      lock.lock();
      if (stack.size() == 0) {
        System.out.println(Thread.currentThread().getName() + " wait on stack empty");
        stackEmptyCondition.await();
      }
      return stack.pop();
    } finally {
      stackFullCondition.signalAll();
      lock.unlock();
    }
  }

  public static void main(String[] args) {
    final int threadCount = 2;
    ReentrantLockWithCondition object = new ReentrantLockWithCondition();
    final ExecutorService service = Executors.newFixedThreadPool(threadCount);
    service.execute(
        () -> {
          for (int i = 0; i < 10; i++) {
            try {
              object.pushToStack("Item " + i);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        });

    service.execute(
        () -> {
          for (int i = 0; i < 10; i++) {
            try {
              System.out.println("Item popped " + object.popFromStack());
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        });

    service.shutdown();
  }
}
