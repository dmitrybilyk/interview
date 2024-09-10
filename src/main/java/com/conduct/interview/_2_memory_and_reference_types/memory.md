## Java Memory Model

The Java Memory Model is divided into two key areas: **Stack memory** and **Heap memory**.

### 1. Stack Memory

Stack memory is allocated per thread, holding:
- **Local variables**: Primitives and references to objects used within the method.
- **Method calls**: Each thread has its own stack for executing method calls.

When the stack exceeds its allocated space, a **StackOverflowError** occurs.

### 2. Heap Memory

Heap memory is shared across all threads and stores:
- **Objects**: All objects with active references are stored here.
- **Instance variables**: Non-static fields of a class.

The heap is further divided into:

- **Young Generation**: Where newly created objects are stored. It includes:
  - **Eden Space**: Where all new objects are first created.
  - **Survivor Spaces**: Areas where objects are moved after surviving garbage collection.

- **Old Generation**: Holds long-lived objects that have survived multiple garbage collection  
  cycles in the young generation.

- **Metaspace**: Introduced in Java 8, this area dynamically stores metadata about classes,  
  internal JDK libraries, and runtime information.

### Static Members

- **Static fields** and **static methods** of a class are stored in the **Metaspace**  
  (post-Java 8). These members are associated with the class itself, rather than with  
  any specific instance, and are loaded once when the class is loaded into memory.

---

This memory structure helps Java efficiently manage object lifecycle, garbage collection,  
and multithreading.
