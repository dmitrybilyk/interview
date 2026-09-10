package com.conduct.interview.coding.leetcode._2_sliding_window.max_profit;

// LC 121 — Best Time to Buy and Sell Stock
// Задача: знайти максимальний прибуток від однієї купівлі + одного продажу акції.
// Ідея: йдемо по цінах зліва направо, весь час пам'ятаємо мінімальну ціну,
// яку бачили ДО поточного дня — саме в цей день найвигідніше було б купити.
// Час O(n), Пам'ять O(1)

// Важливо: maxProfit ініціалізуємо саме як 0, а не Integer.MIN_VALUE.
// Це гарантує правильний результат (0) для порожніх масивів або коли ціни лише падають.
// Використання if (price < minPrice) замість Math.min запобігає зайвим обчисленням прибутку в день купівлі.
public class MaxProfit {
    public static int maxProfit(int[] prices) {
        Integer maxProfit = 0;
        // найбільший прибуток, знайдений на цей момент

        Integer minPrice = Integer.MAX_VALUE;
        // найменша ціна, яку бачили серед усіх попередніх днів (день "купівлі")

        for(int price : prices) {
            if (price < minPrice) {
                minPrice = price;
                // знайшли нову найнижчу ціну — оновлюємо точку "купівлі"
            }
            maxProfit = Math.max(maxProfit, price - minPrice);
            // якби продали сьогодні за price, купивши по minPrice — рахуємо прибуток
            // і залишаємо кращий з двох варіантів
        }
        return maxProfit;
    }

    public static void main(String[] args) {
        System.out.println(maxProfit(new int[]{7, 1, 3, 4, 6}));
    }
}
