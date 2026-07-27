## Java Memory Model (Execution Perspective)

The JVM divides runtime data memory into two fundamental execution areas:
**Stack memory** (thread execution) and **Heap memory** (shared data storage).

---

### 1. Stack Memory (Where Execution Happens)

Stack memory is strictly **thread-contained** and operates on a Last-In,
First-Out (LIFO) structure. It manages the active execution of methods.

* **Thread Isolation:** Each thread has its own private Stack. One thread cannot
  access or corrupt the Stack of another.
* **Stack Frames:** Every time a method is called, a new *Stack Frame* is pushed
  onto the stack. This frame stores:
  * **Local Variables:** Primitive types (`int`, `char`, etc.) and the
    **references (memory addresses)** pointing to actual objects on the Heap.
* **Automatic Lifecycle:** When a method finishes executing, its stack frame is
  automatically popped off and destroyed. Memory is reclaimed immediately
  without involving the Garbage Collector.

> 🔴 **Fatal Error:** If thread execution goes too deep (e.g., infinite
> recursion), the stack space limit is breached, throwing a
> **`StackOverflowError`**.

---

### 2. Heap Memory (Where Objects Live)

Heap memory is a unified runtime data area created on JVM startup. It is
**shared across all threads**.

* **Object Storage:** Stores all instances of classes, interfaces, and arrays.
* **Instance Fields:** Non-static fields of a class (both primitives and object
  references) live directly inside their parent object on the Heap.
* **Garbage Collection:** Memory is managed non-deterministically by the
  Garbage Collector (GC) only after an object becomes unreachable.

> 🔴 **Fatal Error:** If the heap runs out of allocatable space for new objects,
> the JVM throws an **`OutOfMemoryError: Java heap space`**.

---

### 🔀 Stack vs. Heap: The Reference Link

* **Local Variable Reference:** `User user = new User();`
  * The variable `user` is a reference (pointer) living on the **Stack**.
  * The actual `User` instance data lives on the **Heap**.
* **Object Property Reference:** If `User` has a field `Address address;`
  * The `address` reference variable lives inside the `User` object on the
    **Heap**.

---

### 3. Heap Structure & Generational GC

The JVM structures the Heap into distinct zones to optimize Garbage Collection
based on the rule that **most objects die young**.



#### A. Young Generation (Short-Lived Objects)
Designed for high-frequency, lightning-fast cleanup of short-lived objects.
* **Eden Space:** The initial landing zone where all new objects are allocated.
* **Survivor Spaces (S0 / S1):** Two equal spaces used to age objects before
  promoting them to the Old Generation.
  * *How they work:* During a Minor GC, surviving objects from Eden and the
    active Survivor space (`From`) are copied cleanly into the empty Survivor
    space (`To`).
  * The active space and Eden are then completely wiped. This alternation
    completely avoids memory fragmentation.

#### B. Old (Tenured) Generation (Long-Lived Objects)
* Holds long-surviving data, such as Spring beans, caches, or long-running tasks.
* Objects are moved ("promoted") here only after surviving a specific number
  of Minor GC cycles (the aging threshold).
* Cleaned via Major/Full GC cycles, which are significantly heavier and can
  cause application pauses.

#### C. Metaspace (Class Metadata)
* **Off-Heap Memory:** Located in native OS memory, separate from the Heap.
* **Purpose:** Stores runtime class definitions, method metadata, and
  annotations. It scales dynamically based on available system memory.

---

### 🚀 Advanced Optimizations (Great for Senior Interviews)

* **Escape Analysis:** If the Just-In-Time (JIT) compiler proves that an object
  does not escape the scope of the method where it was created, the JVM bypasses
  the Heap entirely. It breaks the object down and allocates it directly on the
  **Stack**, reducing GC overhead.
* **TLAB (Thread-Local Allocation Buffer):** To prevent threads from locking
  each other out while allocating memory on the shared Heap, the JVM gives each
  thread a tiny dedicated slot inside Eden called a TLAB. Threads allocate new
  objects inside their own TLAB safely and concurrently without synchronization
  overhead.

---

### 4. Static Members & Class Data

Static fields and static methods are bound directly to the class itself,
not to any specific instance of that class.

Where they live is split based on the type of data:

* **Class Metadata (Metaspace):** The architectural blueprint of the class
  (method bytecode, annotations, structural layout, and runtime constant pool)
  is stored off-heap in **Metaspace**.
* **Static Fields & References (Heap):** The actual data values of static fields,
  including reference variables pointing to other objects, are stored inside a
  special internal `java.lang.Class` object instance, which lives on the **Heap**.

---

### 🔀 Quick Memory Reference Map

| Data Type | Storage Location | Accessible By |
| :--- | :--- | :--- |
| **Local Variables** | Stack Frame | Owning Thread Only |
| **Instance Fields** | Heap (Inside Object) | All Threads |
| **Static Fields** | Heap (Inside Class Object) | All Threads |
| **Class Bytecode** | Metaspace (Native Memory) | JVM Engine |

This memory structure helps Java efficiently manage object lifecycle,  
garbage collection, and multithreading.