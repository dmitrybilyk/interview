## Java Memory Model

The Java Memory Model is divided into two key areas: **Stack memory** and  
**Heap memory**.

---

### 1. Stack Memory

Stack memory is allocated per thread, holding:

- **Local variables**: Primitives and **references to objects** used within a method
- **Method calls**: Each thread has its own stack for executing method calls

👉 Important:

- References stored in the stack **point to objects in the heap**
- Stack stores only the reference, **not the object itself**

When the stack exceeds its allocated space, a **StackOverflowError** occurs.

---

### 2. Heap Memory

Heap memory is shared across all threads and stores:

- **Objects**: All objects are stored here
- **Instance variables**: Non-static fields of a class
- **References inside objects**: Fields that reference other objects are also stored here

👉 Important:

- If a reference is a **field of an object**, it lives in the heap (inside that object)

---

### Heap Structure

The heap is further divided into:

- **Young Generation**: Where newly created objects are stored. It includes:
    - **Eden Space**: Where all new objects are first created
    - **Survivor Spaces**: Areas where objects are moved after surviving garbage collection

- **Old Generation**: Holds long-lived objects that have survived multiple garbage  
  collection cycles in the young generation

- **Metaspace**: Introduced in Java 8, this area dynamically stores **class metadata**,  
  internal JDK structures, and runtime information

---

### Static Members

- **Static fields** and **static methods** are associated with the class, not instances

👉 Important correction:

- **Class metadata** → stored in **Metaspace**
- **Static fields (including references)** → stored in the **Heap**

---

### 🔑 Key Takeaway

- **Stack** → local variables (including references)
- **Heap** → objects, instance fields, and static fields

👉 Rule:

> **References are stored wherever their variable is defined**  
> (stack for local variables, heap for fields and static members)

---

This memory structure helps Java efficiently manage object lifecycle,  
garbage collection, and multithreading.