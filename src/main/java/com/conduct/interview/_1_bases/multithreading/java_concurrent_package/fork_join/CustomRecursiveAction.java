package com.conduct.interview._1_bases.multithreading.java_concurrent_package.fork_join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class CustomRecursiveAction extends RecursiveAction {

  public static void main(String[] args) {
    int proc = Runtime.getRuntime().availableProcessors();
    System.out.println("availableProcessors - " + proc);
    ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    forkJoinPool.invoke(new CustomRecursiveAction("Some big string"));
  }

  private String workLoad = "";
  private static final int THRESHOLD = 4;

  public CustomRecursiveAction(String workLoad) {
    this.workLoad = workLoad;
  }

  @Override
  protected void compute() {

    if (workLoad.length() > THRESHOLD) {
      ForkJoinTask.invokeAll(createSubtasks());
    } else {
      processing(workLoad);
    }
  }

  private Collection<CustomRecursiveAction> createSubtasks() {

    List<CustomRecursiveAction> subtasks = new ArrayList<>();

    String partOne = workLoad.substring(0, workLoad.length() / 2);
    String partTwo = workLoad.substring(workLoad.length() / 2, workLoad.length());

    subtasks.add(new CustomRecursiveAction(partOne));
    subtasks.add(new CustomRecursiveAction(partTwo));

    return subtasks;
  }

  private void processing(String work) {
    String result = work.toUpperCase();
    System.out.println(
        "This result - (" + result + ") - was processed by " + Thread.currentThread().getName());
  }
}
