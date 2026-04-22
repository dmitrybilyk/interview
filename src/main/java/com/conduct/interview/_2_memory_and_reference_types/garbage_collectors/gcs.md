## Java Garbage Collectors — Quick Cheat Sheet

### GC Types (very short)

- **Serial GC**
    - Single-threaded, Stop-The-World (STW)
    - For small apps / low memory

- **Parallel GC**
    - Multi-threaded, STW
    - Focus on **throughput**, not pause time

- **G1 GC (Garbage First)**
    - Region-based heap
    - Predictable pause times
    - **Default GC in modern Java**

- **ZGC**
    - Concurrent, **ultra-low pauses (<10ms)**
    - Scales to huge heaps

- **Shenandoah**
    - Concurrent, low pauses
    - Similar goal as ZGC

---

## GC by Java Version

- **Java 8**
    - Default: Parallel GC
    - CMS available (deprecated later)

- **Java 11**
    - Default: **G1 GC**
    - CMS still available (but deprecated)
    - ZGC (experimental)

- **Java 17**
    - Default: **G1 GC**
    - **CMS removed**
    - ZGC, Shenandoah available (production-ready)

- **Java 21**
    - Default: **G1 GC**
    - ZGC, Shenandoah — mature, low-latency options

---

## Why G1 Replaced CMS

**CMS (Concurrent Mark-Sweep) problems:**
- ❌ Memory fragmentation (no compaction)
- ❌ Unpredictable pauses
- ❌ "Concurrent mode failure" → fallback to Full GC
- ❌ Hard to tune

**G1 advantages:**
- ✅ **Compaction** (no fragmentation)
- ✅ **Predictable pause time goals**
- ✅ Region-based → more flexible GC
- ✅ Better for large heaps

👉 Result: CMS was **deprecated in Java 9** and **removed in Java 14**

---

## 🧠 Rule of Thumb

- Default → **G1**
- Max throughput → **Parallel**
- Low latency → **ZGC / Shenandoah**