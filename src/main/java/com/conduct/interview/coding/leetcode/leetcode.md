# LeetCode — всі задачі з кодом

---

## 1. Масиви і хешування

### LC 217 — Contains Duplicate · Easy
**Ідея:** `HashSet.add()` повертає `false` якщо елемент вже є → дублікат знайдено. O(n) / O(n)

```java
public static boolean containsDuplicate(int[] nums) {
    Set<Integer> seen = new HashSet<>();
    for (int n : nums) {
        if (!seen.add(n)) return true;
    }
    return false;
}
```

---

### LC 1 — Two Sum · Easy
**Ідея:** HashMap `значення → індекс`. Для кожного `x` шукаємо `complement = target − x` в мапі. O(n) / O(n)

```java
public static String twoSum(int[] ar, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < ar.length; i++) {
        int complement = target - ar[i];
        if (map.containsKey(complement)) return map.get(complement) + "," + i;
        map.put(ar[i], i);
    }
    return "";
}
```

**Варіант — два вказівники (масив має бути відсортований):** O(n) / O(1)

```java
public static String twoSumSorted(int[] ar, int target) {
    int left = 0, right = ar.length - 1;
    while (left < right) {
        int sum = ar[left] + ar[right];
        if (sum == target)      return left + "," + right;
        else if (sum < target)  left++;
        else                    right--;
    }
    return "";
}
```

---

### LC 238 — Product of Array Except Self · Medium
**Ідея:** Два проходи без ділення. Прохід 1 (→): `result[i]` = добуток усього ЗЛІВА. Прохід 2 (←): домножуємо на suffix = добуток усього СПРАВА. O(n) / O(1)

```
nums:   [1,  2,  3,  4]
після→: [1,  1,  2,  6]   ← prefix
після←: [24, 12, 8,  6]   ← × suffix (4, 3·4=12, 2·12=24...)
```

```java
public static int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    result[0] = 1;
    for (int i = 1; i < n; i++)
        result[i] = result[i - 1] * nums[i - 1];       // prefix

    int suffix = 1;
    for (int i = n - 1; i >= 0; i--) {
        result[i] *= suffix;
        suffix *= nums[i];                               // suffix оновлюємо ПІСЛЯ використання
    }
    return result;
}
```

---

### LC 347 — Top K Frequent Elements · Medium
**Ідея:** Bucket sort: масив відер, де `bucket[f]` = список чисел з частотою `f`. Обходимо з кінця і беремо k штук. O(n) / O(n), краще ніж heap O(n log n)

```java
public static int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int n : nums) freq.merge(n, 1, Integer::sum);

    List<Integer>[] bucket = new List[nums.length + 1];
    for (var e : freq.entrySet()) {
        int f = e.getValue();
        if (bucket[f] == null) bucket[f] = new ArrayList<>();
        bucket[f].add(e.getKey());
    }

    int[] result = new int[k];
    int idx = 0;
    for (int f = bucket.length - 1; f >= 0 && idx < k; f--) {
        if (bucket[f] != null)
            for (int n : bucket[f]) { result[idx++] = n; if (idx == k) break; }
    }
    return result;
}
```

---

## 2. Ковзне вікно

### LC 121 — Best Time to Buy and Sell Stock · Easy
**Ідея:** Йдемо по цінах, пам'ятаємо мінімум зліва. Прибуток = `price − minPrice`. O(n) / O(1)

```java
public static int maxProfit(int[] prices) {
    int maxProfit = 0, minPrice = Integer.MAX_VALUE;
    for (int price : prices) {
        if (price < minPrice) minPrice = price;
        maxProfit = Math.max(maxProfit, price - minPrice);
    }
    return maxProfit;
}
```

---

### LC 3 — Longest Substring Without Repeating Characters · Medium
**Ідея:** Вікно `[left..right]`. При повторному символі `left` стрибає одразу за попереднє входження. O(n) / O(n)

```java
public static int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> lastIndex = new HashMap<>();
    int left = 0, best = 0;
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (lastIndex.containsKey(c) && lastIndex.get(c) >= left)
            left = lastIndex.get(c) + 1;
        lastIndex.put(c, right);
        best = Math.max(best, right - left + 1);
    }
    return best;
}
```

---

## 3. Два вказівники

### LC 125 — Valid Palindrome · Easy
**Ідея:** `left` і `right` сходяться до центру, порівнюємо символи попарно. O(n) / O(1)

```java
public static boolean isPalindrome(String s) {
    int left = 0, right = s.length() - 1;
    while (left < right) {
        if (s.charAt(left++) != s.charAt(right--)) return false;
    }
    return true;
}
```

---

### LC 11 — Container With Most Water · Medium
**Ідея:** Рухаємо той вказівник, де стінка нижча — вона і обмежує об'єм, і є шанс знайти вищу. O(n) / O(1)

```java
public static int maxArea(int[] height) {
    int left = 0, right = height.length - 1, maxArea = 0;
    while (left < right) {
        maxArea = Math.max(maxArea, (right - left) * Math.min(height[left], height[right]));
        if (height[left] < height[right]) left++;
        else                              right--;
    }
    return maxArea;
}
```

---

## 4. Стек

### LC 20 — Valid Parentheses · Easy
**Ідея:** Відкриваюча дужка → push. Закриваюча → pop і перевіряємо пару. Наприкінці стек має бути порожнім. O(n) / O(n)

```java
public static boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    Map<Character, Character> pairs = Map.of(')', '(', ']', '[', '}', '{');
    for (char c : s.toCharArray()) {
        if (pairs.containsValue(c)) {
            stack.push(c);
        } else {
            if (stack.isEmpty() || stack.pop() != pairs.get(c)) return false;
        }
    }
    return stack.isEmpty();
}
```

---

## 5. Бінарний пошук

### LC 704 — Binary Search · Easy
**Ідея:** Ділимо вікно навпіл, відкидаємо половину де target немає. `mid = lo + (hi−lo)/2` захищає від overflow. O(log n) / O(1)

```java
public static int search(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if      (nums[mid] == target) return mid;
        else if (nums[mid] < target)  lo = mid + 1;
        else                          hi = mid - 1;
    }
    return -1;
}
```

---

### LC 33 — Search in Rotated Sorted Array · Medium
**Ідея:** Масив розрізаний і переставлений: `[4,5,6,7,0,1,2]`. Одна з половин завжди відсортована — визначаємо яка, перевіряємо чи target в ній, звужуємо вікно. O(log n) / O(1)

```java
public static int searchRotated(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;

        if (nums[lo] <= nums[mid]) {                         // ліва половина відсортована
            if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
            else                                          lo = mid + 1;
        } else {                                             // права половина відсортована
            if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
            else                                          hi = mid - 1;
        }
    }
    return -1;
}
```

---

## 6. Зв'язний список

### LC 206 — Reverse Linked List · Easy
**Ідея:** Три вказівники `prev / curr / next`. Зберігаємо `next` до перезапису, розвертаємо стрілку, зсуваємось. O(n) / O(1)

```java
public static Node reverse(Node head) {
    Node prev = null, curr = head;
    while (curr != null) {
        Node next = curr.next;   // зберегти перед перезаписом
        curr.next = prev;        // розвернути стрілку
        prev = curr;
        curr = next;
    }
    return prev;                 // нова голова
}
```

---

### LC 21 — Merge Two Sorted Lists · Easy
**Ідея:** Dummy-вузол як стартова точка. Завжди приєднуємо вузол з меншим значенням. Залишок одного зі списків чіпляємо цілком. O(n+m) / O(1)

```java
public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0), curr = dummy;
    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) { curr.next = l1; l1 = l1.next; }
        else                  { curr.next = l2; l2 = l2.next; }
        curr = curr.next;
    }
    curr.next = (l1 == null) ? l2 : l1;
    return dummy.next;
}
```

---

### LC 141 — Linked List Cycle · Easy
**Ідея:** Floyd: `slow` (+1 крок), `fast` (+2 кроки). Якщо циклу немає — fast дійде до `null`. Якщо є — вони зустрінуться всередині циклу. O(n) / O(1)

```java
public static boolean hasCycle(Node head) {
    Node slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}
```

---

## 7. Дерева

### LC 226 — Invert Binary Tree · Easy
**Ідея:** Рекурсивно інвертуємо ліве і праве піддерева, потім міняємо їх місцями. O(n) / O(h)

```java
public static TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    TreeNode tmp  = root.left;
    root.left     = invertTree(root.right);
    root.right    = invertTree(tmp);
    return root;
}
```

---

### LC 104 — Maximum Depth of Binary Tree · Easy
**Ідея:** `1 + max(глибина_ліво, глибина_право)`. Базовий випадок — `null` → 0. O(n) / O(h)

```java
public static int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```

---

### LC 102 — Binary Tree Level Order Traversal · Medium
**Ідея:** BFS з чергою. Перед кожним рівнем фіксуємо `queue.size()` — це кількість вузлів на поточному рівні. Обробляємо рівно стільки вузлів, решта — вже наступний рівень. O(n) / O(n)

```java
public static List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Deque<TreeNode> queue = new ArrayDeque<>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        int size = queue.size();                 // фіксуємо розмір ДО додавання дітей
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left  != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
```

---

## 8. Інтервали

### LC 56 — Merge Intervals · Medium
**Ідея:** Сортуємо за початком. Жадібно: якщо наступний інтервал перетинається з `current` — розширюємо його кінець; ні — зберігаємо і беремо наступний. O(n log n) / O(n)

```java
public static int[][] merge(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    List<int[]> result = new ArrayList<>();
    int[] current = intervals[0];
    for (int i = 1; i < intervals.length; i++) {
        if (intervals[i][0] <= current[1])
            current[1] = Math.max(current[1], intervals[i][1]);   // зливаємо
        else { result.add(current); current = intervals[i]; }      // зберігаємо, беремо новий
    }
    result.add(current);
    return result.toArray(new int[0][]);
}
```

---

## 9. Динамічне програмування

### LC 509 — Fibonacci · Easy
**Ідея:** `Stream.iterate` з парою `[a, b]` → `[b, a+b]`. Функціональний стиль, пам'ять O(1). O(n) / O(1)

```java
Stream.iterate(new long[]{0, 1}, f -> new long[]{f[1], f[0] + f[1]})
    .limit(n)
    .map(f -> f[0])
    .forEach(System.out::println);
```

---

### LC 70 — Climbing Stairs · Easy
**Ідея:** Щоб дістатись сходинки `n` — останній крок з `n−1` або `n−2`. Тобто `ways(n) = ways(n−1) + ways(n−2)` — Фібоначчі. O(n) / O(1)

```java
public static int climbStairs(int n) {
    if (n <= 2) return n;
    int a = 1, b = 2;
    for (int i = 3; i <= n; i++) { int c = a + b; a = b; b = c; }
    return b;
}
```

---

### LC 53 — Maximum Subarray (Kadane) · Medium
**Ідея:** `current = max(nums[i], current + nums[i])`. Якщо накопичений "хвіст" від'ємний — починаємо новий підмасив з нуля. O(n) / O(1)

```java
public static int maxSubArray(int[] nums) {
    int max = nums[0], current = nums[0];
    for (int i = 1; i < nums.length; i++) {
        current = Math.max(nums[i], current + nums[i]);
        max = Math.max(max, current);
    }
    return max;
}
// [-2,1,-3,4,-1,2,1,-5,4] → 6  (підмасив [4,-1,2,1])
```

---

### LC 322 — Coin Change · Medium
**Ідея:** `dp[i]` = мінімальна кількість монет для суми `i`. Заповнюємо від 0 до amount. `dp[0]=0`, решта = `amount+1` (умовна нескінченність). O(amount × |coins|) / O(amount)

```java
public static int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);
    dp[0] = 0;
    for (int i = 1; i <= amount; i++)
        for (int coin : coins)
            if (coin <= i) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
    return dp[amount] > amount ? -1 : dp[amount];
}
// coins=[1,5,6,9], amount=11 → 2  (5+6)
```

---

## 10. Граф

### LC 200 — Number of Islands · Medium
**Ідея:** Знайшли `'1'` — це новий острів, лічильник++. DFS "топить" весь острів (`'1'→'0'`), щоб не порахувати вдруге. O(m×n) / O(m×n)

```java
public static int numIslands(char[][] grid) {
    int count = 0;
    for (int r = 0; r < grid.length; r++)
        for (int c = 0; c < grid[0].length; c++)
            if (grid[r][c] == '1') { dfs(grid, r, c); count++; }
    return count;
}

private static void dfs(char[][] grid, int r, int c) {
    if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') return;
    grid[r][c] = '0';
    dfs(grid, r+1, c); dfs(grid, r-1, c);
    dfs(grid, r, c+1); dfs(grid, r, c-1);
}
```

---

## 11. Heap (купа)

### LC 215 — Kth Largest Element · Medium
**Ідея:** Min-heap розміром рівно `k`. Корінь = найменший з k найбільших = k-й найбільший. При `size > k` викидаємо мінімум. O(n log k) / O(k)

```java
public static int findKthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    for (int n : nums) {
        minHeap.offer(n);
        if (minHeap.size() > k) minHeap.poll();
    }
    return minHeap.peek();
}
```

---

## 12. Design

### LC 146 — LRU Cache · Medium
**Ідея:** HashMap для O(1) доступу за ключем + двозв'язний список для O(1) переміщення/видалення. Dummy `head`/`tail` спрощують операції на краях. O(1) get/put

```java
class LruCache {
    class Node { int key, value; Node prev, next; Node(int k, int v) { key=k; value=v; } }

    private Node head = new Node(0,0), tail = new Node(0,0);
    private Map<Integer, Node> cache = new HashMap<>();
    private int capacity;

    public LruCache(int capacity) {
        this.capacity = capacity;
        head.next = tail; tail.prev = head;
    }

    public int get(int key) {
        Node n = cache.get(key);
        if (n == null) return -1;
        moveToFront(n);
        return n.value;
    }

    public void put(int key, int value) {
        Node n = cache.get(key);
        if (n != null) { n.value = value; moveToFront(n); }
        else {
            n = new Node(key, value);
            cache.put(key, n);
            addToFront(n);
            if (cache.size() > capacity) { cache.remove(tail.prev.key); remove(tail.prev); }
        }
    }

    private void moveToFront(Node n) { remove(n); addToFront(n); }
    private void addToFront(Node n)  { n.next = head.next; n.prev = head; head.next.prev = n; head.next = n; }
    private void remove(Node n)      { n.prev.next = n.next; n.next.prev = n.prev; }
}
```

---

### LC 14 — Longest Common Prefix · Easy
**Ідея:** Перший рядок — кандидат на префікс. Вкорочуємо його справа доки кожен рядок не починається з нього. O(n×m) / O(1)

```java
public static String longestCommonPrefix(String[] strs) {
    String prefix = strs[0];
    for (int i = 1; i < strs.length; i++)
        while (!strs[i].startsWith(prefix))
            prefix = prefix.substring(0, prefix.length() - 1);
    return prefix;
}
// ["flower","flow","flight"] → "fl"
```

---

## Патерни — коли що

| Патерн | Сигнал в задачі |
|--------|----------------|
| HashMap / HashSet | дублікати, частоти, "два елементи дають X" |
| Ковзне вікно | "найдовший/найкоротший підрядок з умовою" |
| Два вказівники | відсортований масив, паліндром, два кінці |
| Стек | баланс дужок, "наступний більший", вкладеність |
| Бінарний пошук | відсортоване або "знайди мінімум/межу" |
| DFS (рекурсія) | дерева, граф, flood-fill |
| BFS (черга) | найкоротший шлях, обхід по рівнях |
| DP | "кількість способів", "мінімальна вартість", Фібоначчі |
| Min-heap розміром k | top-K, k-й найбільший |
| Dummy-вузол | зв'язний список — вставка/видалення на краях |

---

## На співбесіді

1. **Уточни** — дублікати? порожній масив? overflow? відсортовано?
2. **Назви brute force** вголос — показує розуміння задачі
3. **Назви патерн** — "тут sliding window, бо..."
4. **Напиши → простеж** на прикладі вручну після написання
5. **Назви складність** — завжди O(час) і O(пам'ять) наприкінці
