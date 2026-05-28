# 🚀 Java Garbage Collectors — Interview Cheat Sheet

## 1. Core Trade-Offs (The "Big Three")

Every GC choice is a deliberate compromise between **Throughput** (processing
power) and **Latency** (pause times).

| Collector | Core Metric | How It Works | Best Use Case |
| :--- | :--- | :--- | :--- |
| **Parallel GC** | **Max Throughput** | Multi-threaded **Stop-The-World (STW)**. Full focus on crunching data; stops the app completely to clean efficiently. | Background batch processing, calculations, offline pipelines. |
| **G1 GC** | **Balanced** | Divided into thousands of **Dynamic Regions**. Compacts memory to prevent fragmentation. Bounded, predictable pause times. | **Default choice** for standard enterprise APIs and web apps. |
| **ZGC** | **Ultra-Low Latency** | Performs cleaning and compaction **concurrently** while your code is running. Pauses are **sub-millisecond (<1ms)**. | High-frequency trading, payment gateways, huge heaps (32GB+). |

> 📌 **What about Serial and Shenandoah?**
> * **Serial GC:** Single-threaded STW. Use only for tiny CLI tools or
    >   tightly constrained containers (<2GB RAM).
> * **Shenandoah:** RedHat’s concurrent collector. Similar goals to ZGC, but
    >   ZGC is the primary Oracle-backed focus for modern low-latency.

---

## 2. Dynamic Memory Layout Evolution

Interviewers love asking how memory *physically* looks under the hood. It
changed dramatically after Java 8:

* **Classic Model (Java 8 & Older):** Strict, continuous, fixed-size physical
  blocks of memory for Eden, Survivor (S0/S1), and Old generation.
* **Modern Model (Java 9+ with G1/ZGC):** The heap is fragmented into thousands
  of independent **Regions** (1MB to 32MB).
    * A region is assigned a logical role (**Eden, Survivor, Old**) dynamically
      on-the-fly.
    * Includes **Humongous Regions** to hold massive objects directly in the
      Old generation without copying them around.



---

## 3. The Interview Timeline (Version Changes)

If they ask *"Which GC was default when?"* or *"What happened to CMS?"*, give
them this clean progression:

* **Java 8:** Default was **Parallel GC**. Class metadata lived in **Metaspace** (Native Memory), completely replacing the old **PermGen** space.
* **Java 11:** Default changed to **G1 GC**. CMS was deprecated.
* **Java 17:** Default remains **G1 GC**. Old-school **CMS was completely
  removed**. ZGC became production-ready.
* **Java 21 / 25:** Default remains **G1 GC**. **Generational ZGC** is
  introduced (`-XX:+UseZGC -XX:+ZGenerational`), separating short-lived and
  long-lived objects within a concurrent architecture for higher throughput.

---

## 4. Why CMS was Executed (The G1 Kill Shot)

If an interviewer asks why **CMS (Concurrent Mark-Sweep)** was killed and
replaced by **G1**:

* ❌ **CMS had no Compaction:** It left memory full of holes (fragmentation),
  eventually causing massive, unpredictable **Full GC** spikes.
* ❌ **Concurrent Mode Failure:** If allocations happened faster than CMS could
  clean, the JVM would panic, freeze completely, and run a single-threaded
  fallback Full GC.
* ✅ **G1 compacts by default** because it evacuates entire regions into new
  ones, leaving zero fragmentation behind. It also lets you set a maximum
  target pause time (`-XX:MaxGCPauseMillis`).

---

## 💡 Quick Interview Blueprint Answers

* **Q: "What GC do you use in production on JDK 21?"**
    * *A:* "We use the default **G1 GC**. It divides the heap into dynamic
      regions rather than continuous blocks, providing an excellent balance of
      high throughput and predictable pause times for our microservices."
* **Q: "When would you switch away from the default?"**
    * *A:* "I would switch to **Parallel GC** if I am running a non-user-facing
      background batch job where raw throughput matters and pauses don't impact
      anyone. I would switch to **Generational ZGC** if I have an ultra-low
      latency requirement or a massive heap where stop-the-world pauses over 1ms
      cannot be tolerated."