package com.conduct.interview._2_memory_and_reference_types.reference_types.strong_reference;

public class StrongReferenceExample {
  public static void main(String[] args) {
    // Creating a strong reference
    MyObject obj = new MyObject();

    // The object is not eligible for garbage collection as long as 'obj' references it
    System.out.println("Strong reference to obj: " + obj);

    // Dereference the strong reference
    obj = null;

    // Now the object is eligible for garbage collection
    System.gc(); // Suggesting the JVM to perform garbage collection

    System.out.println("Strong reference removed, object eligible for GC.");
  }
}

class MyObject {
  @Override
  public String toString() {
    return "This is a strongly referenced object.";
  }
}
