package com.conduct.interview.coding.leetcode._3_two_pointers.container_with_most_water;

// LC 11 — Container With Most Water
// Pattern: Two pointers from both ends, greedily move the shorter wall inward.
// Time O(n), Space O(1)

// Задача: дано масив висот стінок. Знайти дві стінки, які разом з віссю X
// утворюють контейнер, що вміщує найбільше води.
// Ідея: вказівники left і right стоять на початку і в кінці масиву.
// Площа = відстань між ними * менша з двох висот.
// Рухаємо той вказівник, де стінка нижча — бо саме вона обмежує обсяг,
// і є шанс знайти стінку вищу за неї.
public class ContainerWithMostWater {

    public static int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int maxArea = 0;

        while (left < right) {
            int width = right - left;
            int minHeight = Math.min(height[left], height[right]);
            maxArea = Math.max(maxArea, width * minHeight);

            if (height[left] < height[right]) {
                left++;
                // ліва стінка нижча -> вона обмежує площу, рухаємо left вправо
            } else {
                right--;
                // права стінка нижча (або рівна) -> рухаємо right вліво
            }
        }

        return maxArea;
    }

    public static void main(String[] args) {
        System.out.println(maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7})); // 49
    }
}
