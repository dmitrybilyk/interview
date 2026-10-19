package com.conduct.interview.coding.leetcode._3_two_pointers.container_with_most_water;

public class ContainerWithMostWaterCheck3 {
    public static void main(String[] args) {
        System.out.println(maxAreaCheck(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7})); // 49

    }

    private static int maxAreaCheck(int[] ints) {
        int maxArea = 0;

        int left = 0; int right = ints.length - 1;

        while (left < right) {
            int leftHeight = ints[left];
            int rightHeight = ints[right];

            int minHeight = Math.min(leftHeight, rightHeight);
            int width = right - left;
            maxArea = Math.max(maxArea, minHeight * width);

            if (leftHeight <= rightHeight) {
                left++;
            } else {
                right--;
            }
        }

        return maxArea;
    }
}
