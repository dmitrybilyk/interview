package com.conduct.interview.leetcode;

public class MaxProfit {
    public static int maxProfit(int[] prices) {
        Integer maxProfit = 0;
        Integer minPrice = Integer.MAX_VALUE;

        for(int price : prices) {
            if (price < minPrice) {
                minPrice = price;
            }
            maxProfit = Math.max(maxProfit, price - minPrice);
        }
        return maxProfit;
    }

    public static void main(String[] args) {
        System.out.println(maxProfit(new int[]{7, 1, 3, 4, 6}));
    }
}
