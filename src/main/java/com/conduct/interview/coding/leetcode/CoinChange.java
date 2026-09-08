package com.conduct.interview.coding.leetcode;

import java.util.Arrays;

// LC 322 — Coin Change
// Pattern: Bottom-up DP — dp[i] = min coins to make amount i
// Time O(amount * coins), Space O(amount)
public class CoinChange {

    public static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // sentinel "infinity"
        dp[0] = 0;
        for (int i = 1; i <= amount; i++)
            for (int coin : coins)
                if (coin <= i) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
        return dp[amount] > amount ? -1 : dp[amount];
    }

    public static void main(String[] args) {
        System.out.println(coinChange(new int[]{1, 5, 6, 9}, 11)); // 2  (5+6)
        System.out.println(coinChange(new int[]{2}, 3));            // -1
    }
}
