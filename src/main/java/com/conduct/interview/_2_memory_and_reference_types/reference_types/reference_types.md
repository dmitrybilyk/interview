## Reference Types in Java

Java provides different types of references that control how objects are managed and removed  
by the Garbage Collector (GC). These include strong, weak, soft, and phantom references.

### 1. Strong Reference

- **Default behavior** in Java.
- Objects referenced by a strong reference are not eligible for garbage collection unless  
  explicitly dereferenced (e.g., setting the reference to `null`).
- **Example**: Typical object references in your code.

### 2. WeakReference

- **Usage**: Commonly used for referencing objects like class loaders.
- Objects are eligible for GC if **only** weak references point to them.
- **Garbage Collection**: The object is collected as soon as the GC identifies that there are  
  no strong references, making weak references suitable for memory-sensitive caches.

### 3. SoftReference

- **Usage**: Ideal for implementing memory-sensitive caches.
- Objects with soft references are only removed if there is **memory pressure** or  
  low available memory.
- **Garbage Collection**: The object stays in memory as long as there’s sufficient heap space.

### 4. PhantomReference

- **Usage**: Typically used for tracking object finalization or cleanup tasks, knowing when  
  an object is about to be reclaimed by the GC.
- **Garbage Collection**: Objects referenced by a phantom reference have already been  
  finalized, but the memory is not yet reclaimed until the GC explicitly decides to do so.
- A phantom reference doesn’t provide access to the object itself, but it allows  
  you to track when the object is ready to be collected.

---

### Metaphor for Understanding Reference Types

- **Strong Reference**: A customer holding a table indefinitely until they decide to leave.
- **WeakReference**: A customer willing to leave immediately when a new customer arrives.
- **SoftReference**: A customer who will leave only when all tables are full.
- **PhantomReference**: A customer ready to leave when asked, but only when the manager  
  gives final permission.
