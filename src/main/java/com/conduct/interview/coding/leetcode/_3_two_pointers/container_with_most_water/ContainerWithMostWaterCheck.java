package com.conduct.interview.coding.leetcode._3_two_pointers.container_with_most_water;

public class ContainerWithMostWaterCheck {
    public static void main(String[] args) {
        System.out.println(maxAreaCheck(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7})); // 49

    }

    private static int maxAreaCheck(int[] ints) {
        int left = 0, right = ints.length - 1;
        int maxArea = 0;

        while(left < right) {
            int width = right - left;
            int minHeight = Math.min(ints[left], ints[right]);
            maxArea = Math.max(maxArea, width * minHeight);

            if (ints[left] > ints[right]) {
                right--;
            } else {
                left++;
            }
        }

        return maxArea;
    }
}
