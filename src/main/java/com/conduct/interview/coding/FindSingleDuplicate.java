package com.conduct.interview.coding;

import java.util.HashSet;
import java.util.Set;

public class FindSingleDuplicate {

    public static void main(String[] args) {
        int[] nums = {1, 3, 4, 2, 1};
//        System.out.println(findDuplicateFloyed(nums)); // Output: 2
        System.out.println(findDuplicateSet(nums)); // Output: 2
    }

    private static int findDuplicateSet(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            if (set.contains(num)) {
                return num;
            } else {
                set.add(num);
            }
        }
        return -1;
    }

    public static int findDuplicateFloyed(int[] nums) {
        // Phase 1: Detect cycle
        int slow = nums[0];
        int fast = nums[0];

        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);

        // Phase 2: Find entrance to cycle
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }

        return slow; // or fast, both point to duplicate
    }

}
