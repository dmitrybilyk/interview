package com.conduct.interview.coding.leetcode._2_sliding_window.max_profit;

public class MaxProfitCheck {
    public static void main(String[] args) {
        System.out.println(maxProfitCheck(new int[]{7, 1, 3, 4, 6}));
    }

    private static int maxProfitCheck(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;

        for (int price : prices) {
            if (price < minPrice) {
                minPrice = price;
            }
            maxProfit = Math.max(maxProfit, price - minPrice);
        }
        return maxProfit;
    }
}
