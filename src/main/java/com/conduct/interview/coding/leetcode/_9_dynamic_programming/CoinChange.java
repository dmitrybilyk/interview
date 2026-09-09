package com.conduct.interview.coding.leetcode._9_dynamic_programming;

import java.util.Arrays;

// LC 322 — Coin Change
// Pattern: Bottom-up DP — dp[i] = min coins to make amount i
// Time O(amount * coins), Space O(amount)

// Задача: знайти мінімальну кількість монет, щоб набрати суму amount
// (монети можна використовувати необмежену кількість разів).
// Ідея: dp[i] — мінімальна кількість монет для суми i.
// Рахуємо dp від 0 до amount, для кожної суми пробуючи додати кожен номінал монети.
public class CoinChange {

    public static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // sentinel "infinity"
        // amount+1 монет не набереться ніколи (це більше, ніж максимально можлива відповідь) —
        // використовуємо це як умовну "нескінченність" для сум, які ще не вдалось набрати

        dp[0] = 0;
        // суму 0 завжди можна набрати нулем монет

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                    // якщо монету coin можна відняти від i — пробуємо: взяти цю монету
                    // плюс найкращий результат для залишку (i - coin)
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
        // якщо dp[amount] лишився на рівні "нескінченності" — суму набрати неможливо
    }

    public static void main(String[] args) {
        System.out.println(coinChange(new int[]{1, 5, 6, 9}, 11)); // 2  (5+6)
        System.out.println(coinChange(new int[]{2}, 3));            // -1
    }
}
