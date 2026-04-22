# Algorithm Complexity (Big O) Cheat Sheet

### 1. O(1) — Constant Time
**The Logic:** Time stays the same regardless of how much data you have.
* **Simple Example:** Accessing an array element by index or looking up a key in a Hash Map.
* **Code:** `return arr[0]`

### 2. O(log n) — Logarithmic Time
**The Logic:** The dataset is cut in half at every step. Very efficient for large data.
* **Simple Example:** Binary Search (finding a name in a sorted phone book by opening the middle).
* **Code:** A loop where the index is multiplied or divided by 2 each time.

### 3. O(n) — Linear Time
**The Logic:** Time grows exactly in proportion to the data. 10x more data = 10x more time.
* **Simple Example:** Finding a value in an unsorted list.
* **Code:** A single `for` loop.

### 4. O(n log n) — Linearithmic Time
**The Logic:** Usually involves sorting. It is basically performing a $O(\log n)$ operation $n$ times.
* **Simple Example:** The most efficient sorting algorithms like MergeSort or QuickSort.
* **Code:** `list.sort()` (Standard library sorting).

### 5. O(n²) — Quadratic Time
**The Logic:** Time grows exponentially. 10x more data = 100x more time.
* **Simple Example:** Comparing every item in a list with every other item.
* **Code:** Nested loops (a `for` loop inside another `for` loop).

---

## Quick Comparison Table

| Big O | Name | Speed | Memory Hook |
| :--- | :--- | :--- | :--- |
| **O(1)** | Constant | Instant | Direct access |
| **O(log n)** | Logarithmic | Very Fast | Halving the data |
| **O(n)** | Linear | Fair | One loop through data |
| **O(n log n)** | Linearithmic | Good | Efficient Sorting |
| **O(n²)** | Quadratic | Slow | Nested loops |

## Interview Rule of Thumb
- **Count the loops:** - 0 loops = $O(1)$
    - 1 loop = $O(n)$
    - 2 nested loops = $O(n^2)$
- **Searching sorted data:** Usually $O(\log n)$.
- **Sorting data:** Usually $O(n \log n)$.


# Search & Sort Algorithm Reference

## 🔍 Search Algorithms

### **Linear Search** | $O(n)$
* **How it works:** Checks every item one by one.
* **Best for:** Unsorted data.
* **Speed:** slow if the list is huge.

### **Binary Search** | $O(\log n)$
* **How it works:** Splits a **sorted** list in half repeatedly.
* **Best for:** Sorted data only.
* **Speed:** Extremely fast.

---

## 🧹 Sorting Algorithms

### **Quick Sort** | $O(n \log n)$
* **How it works:** Picks a "pivot" and moves smaller items left, larger items right.
* **Average:** $O(n \log n)$
* **Worst:** $O(n^2)$ (if pivot is bad).
* **Note:** Usually the fastest in practice.

### **Merge Sort** | $O(n \log n)$
* **How it works:** Splits the list into single items, then merges them back in order.
* **Complexity:** Always $O(n \log n)$ (very stable).
* **Note:** Uses more memory than Quick Sort.

### **Insertion Sort** | $O(n^2)$
* **How it works:** Builds the sorted list one item at a time (like sorting a hand of cards).
* **Complexity:** $O(n^2)$.
* **Note:** Very fast $O(n)$ if the data is **already almost sorted**.

### **Bubble Sort** | $O(n^2)$
* **How it works:** Swaps adjacent items repeatedly.
* **Note:** Highly inefficient. Only mentioned to show what *not* to use.

---

## 📊 Summary Comparison

| Algorithm | Type | Average Case | Worst Case |
| :--- | :--- | :--- | :--- |
| **Binary Search** | Search | **O(log n)** | O(log n) |
| **Linear Search** | Search | **O(n)** | O(n) |
| **Quick Sort** | Sort | **O(n log n)** | O(n^2) |
| **Merge Sort** | Sort | **O(n log n)** | O(n log n) |
| **Insertion Sort**| Sort | **O(n^2)** | O(n^2) |
| **Bubble Sort** | Sort | **O(n^2)** | O(n^2) |

## 💡 Interview Tips
1.  **Sorted Data?** Use Binary Search.
2.  **General Sorting?** Use Quick Sort or Merge Sort.
3.  **Space Constrained?** Quick Sort is better than Merge Sort (uses less RAM).
4.  **Almost Sorted?** Insertion Sort is actually very efficient.