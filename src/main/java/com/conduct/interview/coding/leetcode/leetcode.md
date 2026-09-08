# LeetCode Top 26 — Java Interview

**Why 26?** Based on Blind 75 / LeetCode frequency data, these problems cover ~80% of what
companies actually ask. They're grouped by pattern — because interviewers don't care which
specific problem you solved; they care whether you recognise the pattern.

---

## Arrays & Hashing

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 1 | Two Sum | Easy | `Sum2Target.java` | HashMap complement lookup — O(n) |
| LC 217 | Contains Duplicate | Easy | `ContainsDuplicate.java` | HashSet — `add()` returns false on duplicate |
| LC 238 | Product of Array Except Self | Medium | `ProductExceptSelf.java` | Prefix pass left → suffix pass right, no division |
| LC 347 | Top K Frequent Elements | Medium | `TopKFrequent.java` | Bucket sort by frequency beats heap O(n log n) |
| LC 1+ | Two Sum — All Approaches | Easy | `Complement.java` | Brute O(n²), HashMap O(n), two-pointer on sorted O(n log n) — shows range |

---

## Sliding Window

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 3 | Longest Substring Without Repeating Characters | Medium | `LongestSubstringNoRepeat.java` | Map of last-seen index; jump left past duplicate |
| LC 121 | Best Time to Buy and Sell Stock | Easy | `MaxProfit.java` | Track min price seen so far; max profit = price − min |

---

## Two Pointers

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 125 | Valid Palindrome | Easy | `Palindrome.java` | Left/right converge, skip non-alphanumeric |
| LC 15 | 3Sum | Medium | `ThreeSum.java` | Sort + fix i, two-pointer pair; skip duplicates |

---

## Stack

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 20 | Valid Parentheses | Easy | `ValidParentheses.java` | Push open, pop+match on close; stack must be empty at end |

---

## Binary Search

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 704 | Binary Search | Easy | `BinarySearch.java` | `mid = lo + (hi-lo)/2` avoids overflow; `lo <= hi` |
| LC 33 | Search in Rotated Sorted Array | Medium | `SearchRotatedArray.java` | Identify which half is sorted, then decide which way to go |

---

## Linked List

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 21 | Merge Two Sorted Lists | Easy | `MergeLinkedLists.java` | Dummy head + advance whichever is smaller |
| LC 206 | Reverse Linked List | Easy | `ReverseLinkedList.java` | 3-pointer iterative: prev/curr/next |
| LC 141 | Linked List Cycle | Easy | `LinkedListCycle.java` | Floyd's slow/fast — they meet iff cycle exists |

---

## Trees

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 226 | Invert Binary Tree | Easy | `InvertBinaryTree.java` | Post-order DFS: swap children after recursing |
| LC 104 | Maximum Depth of Binary Tree | Easy | `MaxDepthBinaryTree.java` | `1 + max(left, right)` — one liner |
| LC 100 | Same Tree | Easy | `TreeNode.java` | Recursive: both null → true; one null → false; vals equal and recurse both sides |

---

## Intervals

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 56 | Merge Intervals | Medium | `MergeIntervals.java` | Sort by start, extend current end greedily |

---

## Dynamic Programming

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 70 | Climbing Stairs | Easy | `ClimbingStairs.java` | Fibonacci pattern: dp[n] = dp[n-1] + dp[n-2] |
| LC 53 | Maximum Subarray | Medium | `MaxSubarray.java` | Kadane's: reset running sum to 0 when negative |
| LC 322 | Coin Change | Medium | `CoinChange.java` | dp[i] = min coins for amount i; fill from 0 up |
| LC 509 | Fibonacci Number | Easy | `Fibonachi.java` | Java Streams: `Stream.iterate` with pair — functional style |

---

## Graphs

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 200 | Number of Islands | Medium | `NumberOfIslands.java` | DFS flood-fill — sink '1'→'0' to mark visited |

---

## Design

| # | Problem | Difficulty | File | Key insight |
|---|---------|-----------|------|-------------|
| LC 146 | LRU Cache | Medium | `LruCache.java` | LinkedHashMap or HashMap + custom doubly-linked list |
| LC 14 | Longest Common Prefix | Easy | `LongestPrefix.java` | Sort and compare first vs last string only |

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
| **Heap** | Top-K, streaming median, scheduling |
| **Bucket sort** | Top-K when values are bounded (beats heap asymptotically) |

---

## What to say in the interview

1. **Clarify** — ask about duplicates, empty input, integer overflow, sorted/unsorted.
2. **Brute force first** — say it out loud even if you won't code it; shows you understand the problem.
3. **State the pattern** — "I'll use a sliding window here because…"
4. **Code, then trace** — walk through your example by hand after writing.
5. **State complexity** — always give O(time) and O(space) at the end.
