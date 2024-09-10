package com.conduct.interview._2_memory_and_reference_types.reference_types.soft_reference;

import java.lang.ref.SoftReference;

public class SoftReferenceExample {
    public static void main(String[] args) {
        // Creating a strong reference
        MyObject obj = new MyObject();
        
        // Creating a soft reference to the strong reference
        SoftReference<MyObject> softRef = new SoftReference<>(obj);

        // The object is still strongly referenced and won't be collected
        System.out.println("Before GC: Soft reference to obj: " + softRef.get());

        // Dereference the strong reference
        obj = null;

        // Suggest garbage collection (the object may or may not be collected)
        System.gc();

        // Since soft references are cleared only when memory is low,
        // the object may still be present in memory
        if (softRef.get() != null) {
            System.out.println("After GC: Soft reference still holds the object.");
        } else {
            System.out.println("After GC: Soft reference has been cleared (object collected).");
        }
    }
}

class MyObject {
    @Override
    public String toString() {
        return "This is a softly referenced object.";
    }
}
