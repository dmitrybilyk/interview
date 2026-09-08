package com.conduct.interview.coding.leetcode._10_graphs;

// LC 200 — Number of Islands
// Pattern: DFS flood-fill — sink each island cell to '0' to avoid revisiting
// Time O(m*n), Space O(m*n) recursion stack

// Задача: порахувати кількість "островів" (груп сусідніх '1', з'єднаних по вертикалі/горизонталі)
// у сітці з '0' (вода) та '1' (суша).
// Ідея flood-fill: знайшовши необроблену клітинку суші — це новий острів;
// далі DFS "затоплює" (перетворює на '0') усі клітинки суші, з'єднані з нею,
// щоб не порахувати їх ще раз.
public class NumberOfIslands {

    public static int numIslands(char[][] grid) {
        int count = 0;
        for (int r = 0; r < grid.length; r++)
            for (int c = 0; c < grid[0].length; c++)
                if (grid[r][c] == '1') { dfs(grid, r, c); count++; }
                // знайшли ще не затоплену клітинку суші -> це початок нового острова:
                // затоплюємо весь острів через dfs і збільшуємо лічильник

        return count;
    }

    private static void dfs(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') return;
        // вихід за межі сітки АБО клітинка вже вода/затоплена -> тут зупиняємось

        grid[r][c] = '0'; // sink
        // позначаємо клітинку як відвідану, "затоплюючи" її

        dfs(grid, r+1, c); dfs(grid, r-1, c);
        dfs(grid, r, c+1); dfs(grid, r, c-1);
        // рекурсивно перевіряємо всі 4 сусідні клітинки (вниз, вгору, вправо, вліво)
    }

    public static void main(String[] args) {
        char[][] grid = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        System.out.println(numIslands(grid)); // 3
    }
}
