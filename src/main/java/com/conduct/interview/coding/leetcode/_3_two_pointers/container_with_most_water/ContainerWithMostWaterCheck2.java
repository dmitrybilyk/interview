package com.conduct.interview.coding.leetcode._3_two_pointers.container_with_most_water;

public class ContainerWithMostWaterCheck2 {
    public static void main(String[] args) {
        System.out.println(maxAreaCheck(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7})); // 49

    }

    private static int maxAreaCheck(int[] ints) {
        int left = 0, right = ints.length - 1;
        int maxArea = 0;

        while (left < right) {
            int width = right - left;
            int leftHeight = ints[left];
            int rightHeight = ints[right];
            int minHeight = Math.min(leftHeight, rightHeight);
            maxArea = Math.max(maxArea, width * minHeight);

            if (leftHeight > rightHeight) {
                right--;
            } else {
                left++;
            }
        }
        return maxArea;
    }
}
