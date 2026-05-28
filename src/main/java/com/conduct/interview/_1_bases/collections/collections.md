# 🚀 Java Collections — Ultimate Interview Cheat Sheet

## 1. List

| Collection | Underlying Structure | Time Complexity (Get) | Time Complexity (Insert/Delete) | Best Use Case |
| :--- | :--- | :--- | :--- | :--- |
| **ArrayList** | Dynamic `Object[]` array | **$O(1)$** (Direct index access) | **$O(n)$** in middle (requires element shifting/copying). **$O(1)$** at the end. | **Default choice** for 99% of tasks requiring fast reads and updates at the end. |
| **LinkedList** | Doubly Linked List (Nodes with prev/next pointers) | **$O(n)$** (Must traverse from head or tail) | **$O(1)$** at head/tail. **$O(n)$** in middle due to search time. | Implementing Queues/Deques or heavy modifications strictly at head/tail. |

### 💡 Key Nuances
* **ArrayList Resize:** When full, it creates a new array ($1.5\times$ original size) and copies elements.
* **CPU Cache Friendliness:** ArrayList stores data in contiguous memory blocks, making it highly **CPU cache-friendly**. LinkedList nodes are scattered randomly across the Heap, causing frequent cache misses.
* **Stack:** Extends `Vector`. Obsolete, legacy, synchronized LIFO structure. Use `Deque` implementations instead.
* **Queue / Deque:** Queue is FIFO. Deque is double-ended (FIFO + LIFO operations allowed at both ends).

---

## 2. Map

### HashMap (The Industry Standard)
* **Underlying Structure:** An array of Nodes (`Node[] table`). Default initial capacity is **16**, with a Load Factor of **0.75**.
* **How it works:** Index is calculated using `(capacity - 1) & hash(key)`.
* **Collision Handling (The Tree Evolution):**
    * Originally, collisions formed a standard Linked List ($O(n)$ search).
    * **Modern Java Optimization:** If a bucket reaches **8 elements** (`TREEIFY_THRESHOLD`) and total map capacity is $\ge 64$, the bucket automatically transforms into a **Balanced Tree (Red-Black Tree)**. This drops search time from **$O(n)$** down to **$O(\log n)$**. If elements decrease back to 6, it converts back to a linked list.
* **Memory Traps:** HashMap **never shrinks** its internal array automatically when elements are cleared. To free memory, you must create a new Map copy or clear it completely.

### Other Map Implementations
* **LinkedHashMap:** Extends HashMap but maintains a doubly linked list running through all entries. Preserves **insertion order** or access order.
* **TreeMap:** Implements `SortedMap`. Uses a **Red-Black Tree** structure underneath. Keeps keys sorted in their natural order or via a custom `Comparator`. Operations take **$O(\log n)$** time.
* **Hashtable:** Obsolete, legacy, synchronized equivalent of HashMap. Does not allow `null` keys/values.

---

## 3. Set

Sets guarantee element uniqueness under the hood by wrapping corresponding Map implementations.

* **HashSet:** Uses a `HashMap` internally. Your element acts as the Map's *Key*, and a static dummy `Object` is reused as the *Value*.
* **LinkedHashSet:** Uses a `LinkedHashMap` internally to maintain **insertion order**.
* **TreeSet:** Uses a `TreeMap` underneath to maintain **natural or custom sorted order**.

---

## 4. Concurrent Iteration: Fail-Fast vs. Fail-Safe



### Fail-Fast (e.g., ArrayList, HashMap)
* **Behavior:** Throws `ConcurrentModificationException` instantly if the collection is modified structurally while an iterator loops over it.
* **Mechanism:** Tracks structural modifications using a `modCount` counter. If the iterator notices `modCount` changed mid-flight, it fails fast.
* **The Loophole:** It is safe to remove elements during iteration **only** if you use the iterator's own method: `iterator.remove()`.

### Fail-Safe / Non-Interfering (e.g., CopyOnWriteArrayList, ConcurrentHashMap)
* **Behavior:** Does **not** throw exceptions when structural modifications occur during iteration.
* **Mechanism:** Works on a immutable **snapshot clone** of the collection data state created at the moment the iterator was initialized.
* **Trade-off:** High memory usage footprint. The iterator will not reflect real-time updates made to the collection during the loop execution.