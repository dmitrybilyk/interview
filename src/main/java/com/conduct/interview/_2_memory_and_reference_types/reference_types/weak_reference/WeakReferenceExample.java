package com.conduct.interview._2_memory_and_reference_types.reference_types.weak_reference;

import java.lang.ref.WeakReference;

public class WeakReferenceExample {
  public static void main(String[] args) {
    // Creating a strong reference
    MyObject obj = new MyObject();

    // Creating a weak reference to the strong reference
    WeakReference<MyObject> weakRef = new WeakReference<>(obj);

    // The object is still strongly referenced and won't be collected
    System.out.println("Before GC: Strong reference to obj: " + weakRef.get());

    // Dereference the strong reference
    obj = null;

    // Suggest garbage collection
    System.gc();

    // Now the object is eligible for garbage collection since only weak references remain
    if (weakRef.get() != null) {
      System.out.println("After GC: Weak reference still holds the object.");
    } else {
      System.out.println("After GC: Weak reference has been cleared (object collected).");
    }
  }
}

class MyObject {
  @Override
  public String toString() {
    return "This is a weakly referenced object.";
  }
}
