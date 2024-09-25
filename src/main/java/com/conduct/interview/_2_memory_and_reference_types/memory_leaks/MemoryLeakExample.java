package com.conduct.interview._2_memory_and_reference_types.memory_leaks;

import java.util.ArrayList;
import java.util.List;

public class MemoryLeakExample {
  // Static list holding references to objects
  private static List<MyObject> objectList = new ArrayList<>();

  public static void main(String[] args) {
    //        List<MyObject> objectList = new ArrayList<>();
    // Create objects and add them to the list
    for (int i = 0; i < 1000; i++) {
      MyObject obj = new MyObject("Object " + i);
      objectList.add(obj);
    }

    // Simulate memory leak
    System.out.println("Objects added to the list. Memory leak is happening.");

    // Uncomment the following line to fix the memory leak
    // objectList.clear();

    // Simulate some delay to observe memory usage (can be monitored using tools)
    try {
      Thread.sleep(100000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

class MyObject {
  private String name;

  public MyObject(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
