# 🚀 Java Reference Types — Quick Interview Sheet

Reference types tell the Garbage Collector (GC) how aggressively it can reclaim
an object from memory.

| Reference Type | GC Behavior | Best Use Case |
| :--- | :--- | :--- |
| **Strong** | **Never collected** while active. Will trigger `OutOfMemoryError` instead. | Default for 99% of your normal code. |
| **Soft** | Collected **only during memory pressure** (when heap is almost full). | Memory-sensitive caches (e.g., heavy images/JSON trees). |
| **Weak** | Collected **immediately during the next GC cycle**, no matter what. | `WeakHashMap` or metadata tracking (avoids memory leaks). |
| **Phantom** | Object is already dead (`get()` always returns `null`). Used as a post-mortem notification. | Replacing `finalize()` for low-level native resource cleanup. |

---

## 1. Strong Reference — "The Default Anchor"
* **What it is:** Your everyday object declaration: `User u = new User();`
* **GC Rules:** As long as a strong reference points to an object, the GC will **never** touch it. To make it eligible for collection, you must explicitly break the bond by setting it to `null`.

## 2. SoftReference — "The Safety Net Cache"
* **GC Rules:** Safe during good times. But if the JVM runs out of heap space and is about to crash with an OOM, the GC will forcefully wipe out all Soft References to clear room.
* **Primitive Summary:** It stays alive until a **memory crisis** happens.



## 3. WeakReference — "The Short-Lived Guest"
* **GC Rules:** Completely ignored by the GC. The moment the Garbage Collector runs, if it sees an object held *only* by a Weak Reference, it destroys it instantly.
* **Primitive Summary:** Survives exactly until the **very next GC cycle**.

## 4. PhantomReference — "The Ghost Notification"
* **GC Rules:** You cannot interact with the object anymore. Calling `.get()` always returns `null`. It places a token into a `ReferenceQueue` *after* the object has been finalized.
* **Primitive Summary:** Used strictly as a **"goodbye email"** to clean up off-heap native memory (like file descriptors or direct byte buffers).

---

## 💡 10-Second Interview Blueprint

> * **Strong:** Never deleted.
> * **Soft:** Deleted only if memory is completely full.
> * **Weak:** Deleted during the next immediate GC run.
> * **Phantom:** Already dead; used for low-level post-mortem cleanup.