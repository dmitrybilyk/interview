package com.conduct.interview.coding.leetcode._1_arrays_and_hashing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

// LC 347 — Top K Frequent Elements
// Pattern: Bucket sort by frequency — avoids O(n log n) heap
// Time O(n), Space O(n)

// Задача: знайти K чисел, що зустрічаються найчастіше.
// Ідея bucket sort: замість сортування за частотою (O(n log n) через heap),
// створюємо "відра" — масив, де індекс = частота, а значення = список чисел з такою частотою.
// Частота не може бути більшою за довжину масиву, тому відер рівно nums.length + 1.
public class TopKFrequent {

    @SuppressWarnings("unchecked")
    public static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) freq.merge(n, 1, Integer::sum);
        // рахуємо, скільки разів зустрічається кожне число

        List<Integer>[] bucket = new List[nums.length + 1]; // index = frequency
        // відро[f] містить усі числа, які зустрічаються рівно f разів

        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            int f = e.getValue();
            if (bucket[f] == null) bucket[f] = new ArrayList<>();
            bucket[f].add(e.getKey());
            // кладемо число у відро, що відповідає його частоті
        }

        int[] result = new int[k];
        int idx = 0;
        for (int f = bucket.length - 1; f >= 0 && idx < k; f--)
            // йдемо від найвищої частоти до найнижчої

            if (bucket[f] != null)
                for (int n : bucket[f]) { result[idx++] = n; if (idx == k) break; }
                // забираємо числа з поточного відра, поки не набрали k штук

        return result;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(topKFrequent(new int[]{1,1,1,2,2,3}, 2))); // [1,2]
    }
}
