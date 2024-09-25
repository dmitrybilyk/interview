package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.scheduled_executor_service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorServiceDemo {

  private Task runnableTask;
  private CallableTask callableTask;

  private void tryFuture() throws InterruptedException {
    CallableTask callableTask = new CallableTask();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    Future<String> resultFuture = executorService.schedule(callableTask, 1, TimeUnit.SECONDS);
    try {
      String result = resultFuture.get();
      System.out.println("Result - " + result);
    } catch (InterruptedException | ExecutionException ex) {

    }
    executorService.shutdownNow();
  }

  private void executeWithFixedRate() throws InterruptedException {
    Task runnableTask = new Task();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.scheduleAtFixedRate(runnableTask, 2, 3, TimeUnit.SECONDS);
  }

  private void executeWithFixedDelay() throws InterruptedException {
    Task runnableTask = new Task();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.scheduleWithFixedDelay(runnableTask, 2, 3, TimeUnit.SECONDS);
  }

  private void executeMultipleThreads() throws InterruptedException {
    Task runnableTask = new Task();
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
    // executorService.scheduleWithFixedDelay( runnableTask, 2, 3, TimeUnit.SECONDS);
  }

  public static void main(String... args) throws InterruptedException {
    ScheduledExecutorServiceDemo demo = new ScheduledExecutorServiceDemo();
    // demo.tryFuture();
    // demo.executeWithFixedRate();
    demo.executeWithFixedDelay();
    // demo.executeWithMultiThread();
  }
}

class CallableTask implements Callable<String> {
  @Override
  public String call() throws Exception {
    return "Returning for future";
  }
}

class Task implements Runnable {

  @Override
  public void run() {
    System.out.println("Doing something in runnable");
    // task details
  }
}
