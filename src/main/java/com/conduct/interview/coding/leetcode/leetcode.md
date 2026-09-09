# LeetCode Top 27 — Java Interview

**Why 27?** Based on Blind 75 / LeetCode frequency data, these problems cover ~80% of what
companies actually ask. They're grouped by pattern — because interviewers don't care which
specific problem you solved; they care whether you recognise the pattern.

---

## Arrays & Hashing (`_1_arrays_and_hashing/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 1 | Two Sum | Easy | `_1_arrays_and_hashing/Sum2Target.java` | HashMap complement lookup — O(n) |
| LC 217 | Contains Duplicate | Easy | `_1_arrays_and_hashing/ContainsDuplicate.java` | HashSet — `add()` returns false on duplicate |
| LC 238 | Product of Array Except Self | Medium | `_1_arrays_and_hashing/ProductExceptSelf.java` | Prefix pass left → suffix pass right, no division |
| LC 347 | Top K Frequent Elements | Medium | `_1_arrays_and_hashing/TopKFrequent.java` | Bucket sort by frequency beats heap O(n log n) |
| LC 1+ | Two Sum — Brute Force | Easy | `_1_arrays_and_hashing/two_sum/TwoSumBruteForce.java` | Check every pair — O(n²) |
| LC 1+ | Two Sum — HashMap (indices) | Easy | `_1_arrays_and_hashing/two_sum/TwoSumHashMapIndices.java` | One-pass HashMap, returns indices — O(n) |
| LC 1+ | Two Sum — HashMap (values) | Easy | `_1_arrays_and_hashing/two_sum/TwoSumHashMapValues.java` | Same HashMap idea, returns the values instead — O(n) |
| LC 1+ | Two Sum — Two Pointer (sorted) | Easy | `_1_arrays_and_hashing/two_sum/TwoSumTwoPointerSorted.java` | Converge from both ends — O(n), but input must already be sorted |

---

## Sliding Window (`_2_sliding_window/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 3 | Longest Substring Without Repeating Characters | Medium | `_2_sliding_window/LongestSubstringNoRepeat.java` | Map of last-seen index; jump left past duplicate |
| LC 121 | Best Time to Buy and Sell Stock | Easy | `_2_sliding_window/MaxProfit.java` | Track min price seen so far; max profit = price − min |

---

## Two Pointers (`_3_two_pointers/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 125 | Valid Palindrome | Easy | `_3_two_pointers/Palindrome.java` | Left/right converge, skip non-alphanumeric |
| LC 15 | 3Sum | Medium | `_3_two_pointers/ThreeSum.java` | Sort + fix i, two-pointer pair; skip duplicates |

---

## Stack (`_4_stack/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 20 | Valid Parentheses | Easy | `_4_stack/ValidParentheses.java` | Push open, pop+match on close; stack must be empty at end |

---

## Binary Search (`_5_binary_search/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 704 | Binary Search | Easy | `_5_binary_search/BinarySearch.java` | `mid = lo + (hi-lo)/2` avoids overflow; `lo <= hi` |
| LC 33 | Search in Rotated Sorted Array | Medium | `_5_binary_search/SearchRotatedArray.java` | Identify which half is sorted, then decide which way to go |

---

## Linked List (`_6_linked_list/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 21 | Merge Two Sorted Lists | Easy | `_6_linked_list/MergeLinkedLists.java` | Dummy head + advance whichever is smaller |
| LC 206 | Reverse Linked List | Easy | `_6_linked_list/ReverseLinkedList.java` | 3-pointer iterative: prev/curr/next |
| LC 141 | Linked List Cycle | Easy | `_6_linked_list/LinkedListCycle.java` | Floyd's slow/fast — they meet iff cycle exists |

---

## Trees (`_7_trees/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 226 | Invert Binary Tree | Easy | `_7_trees/InvertBinaryTree.java` | Post-order DFS: swap children after recursing |
| LC 104 | Maximum Depth of Binary Tree | Easy | `_7_trees/MaxDepthBinaryTree.java` | `1 + max(left, right)` — one liner |
| LC 100 | Same Tree | Easy | `_7_trees/TreeNode.java` | Recursive: both null → true; one null → false; vals equal and recurse both sides |
| LC 102 | Binary Tree Level Order Traversal | Medium | `_7_trees/LevelOrderTraversal.java` | BFS with Queue; snapshot `queue.size()` before processing each level |

---

## Intervals (`_8_intervals/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 56 | Merge Intervals | Medium | `_8_intervals/MergeIntervals.java` | Sort by start, extend current end greedily |

---

## Dynamic Programming (`_9_dynamic_programming/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 70 | Climbing Stairs | Easy | `_9_dynamic_programming/ClimbingStairs.java` | Fibonacci pattern: dp[n] = dp[n-1] + dp[n-2] |
| LC 53 | Maximum Subarray | Medium | `_9_dynamic_programming/MaxSubarray.java` | Kadane's: reset running sum to 0 when negative |
| LC 322 | Coin Change | Medium | `_9_dynamic_programming/CoinChange.java` | dp[i] = min coins for amount i; fill from 0 up |
| LC 509 | Fibonacci Number | Easy | `_9_dynamic_programming/Fibonachi.java` | Java Streams: `Stream.iterate` with pair — functional style |

---

## Heap (`_12_heap/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 215 | Kth Largest Element in an Array | Medium | `_12_heap/KthLargest.java` | Min-heap of size k; root = kth largest; evict smallest when size > k |

---

## Graphs (`_10_graphs/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 200 | Number of Islands | Medium | `_10_graphs/NumberOfIslands.java` | DFS flood-fill — sink '1'→'0' to mark visited |

---

## Design (`_11_design/`)

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 146 | LRU Cache | Medium | `_11_design/LruCache.java` | LinkedHashMap or HashMap + custom doubly-linked list |
| LC 14 | Longest Common Prefix | Easy | `_11_design/LongestPrefix.java` | Sort and compare first vs last string only |

---

## Pattern cheat-sheet

| Pattern | When to reach for it |
|---------|----------------------|
| **HashMap/Set** | "Two elements summing to X", duplicates, frequency |
| **Sliding window** | "Longest/shortest subarray/substring satisfying condition" |
| **Two pointers** | Sorted array, partition, palindrome, remove duplicates |
| **Stack** | Matching brackets, next-greater-element, monotonic problems |
| **Binary search** | Sorted input or "find boundary/minimum in X" |
| **DFS/BFS** | Graphs, trees, flood-fill, connected components |
| **DP (1D)** | "Number of ways", "min cost", overlapping subproblems |
| **Prefix product/sum** | Array where division is banned or range queries needed |
| **Heap** | Top-K, kth largest, scheduling — PriorityQueue, min-heap of size k |
| **Bucket sort** | Top-K when values are bounded (beats heap asymptotically) |

---

## What to say in the interview

1. **Clarify** — ask about duplicates, empty input, integer overflow, sorted/unsorted.
2. **Brute force first** — say it out loud even if you won't code it; shows you understand the problem.
3. **State the pattern** — "I'll use a sliding window here because…"
4. **Code, then trace** — walk through your example by hand after writing.
5. **State complexity** — always give O(time) and O(space) at the end.
