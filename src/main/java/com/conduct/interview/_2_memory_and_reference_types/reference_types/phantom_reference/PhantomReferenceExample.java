package com.conduct.interview._2_memory_and_reference_types.reference_types.phantom_reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public class PhantomReferenceExample {
  public static void main(String[] args) {
    // Create a reference queue
    ReferenceQueue<MyObject> refQueue = new ReferenceQueue<>();

    // Create a strong reference to the object
    MyObject obj = new MyObject();

    // Create a phantom reference to the object, associated with the reference queue
    PhantomReference<MyObject> phantomRef = new PhantomReference<>(obj, refQueue);

    // The object is still strongly referenced and won't be collected
    System.out.println("Before GC: Phantom reference to obj: " + phantomRef.get());

    // Dereference the strong reference
    obj = null;

    // Suggest garbage collection
    System.gc();

    // After GC, the object is eligible for collection, and the phantom reference should be enqueued
    if (phantomRef.isEnqueued()) {
      System.out.println("Phantom reference has been enqueued.");
      // Check if the reference has been added to the queue
      PhantomReference<MyObject> refFromQueue = (PhantomReference<MyObject>) refQueue.poll();
      if (refFromQueue != null) {
        System.out.println("Object was enqueued in the reference queue.");
      } else {
        System.out.println("Reference queue is empty.");
      }
    } else {
      System.out.println("Phantom reference has not been enqueued yet.");
    }
  }
}

class MyObject {
  @Override
  public String toString() {
    return "This is a phantom referenced object.";
  }
}
