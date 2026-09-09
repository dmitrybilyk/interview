package com.conduct.interview.coding.leetcode._7_trees;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// LC 102 — Binary Tree Level Order Traversal
// Pattern: BFS with a Queue — snapshot level size before processing children
// Time O(n), Space O(n)

// Задача: обійти бінарне дерево рівень за рівнем і повернути список списків —
// кожен внутрішній список містить значення вузлів одного рівня.
// Ідея: використовуємо чергу (Queue). Перед обробкою кожного рівня фіксуємо
// її розмір — це кількість вузлів на поточному рівні. Обробляємо рівно стільки
// вузлів і додаємо їхніх дітей у хвіст черги (вони стануть наступним рівнем).
public class LevelOrderTraversal {

    static class TreeNode { int val; TreeNode left, right; TreeNode(int v) { val = v; } }

    public static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();

        if (root == null) {
            return result;
        }

        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        // кладемо корінь у чергу — старт обходу

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            // фіксуємо кількість вузлів на поточному рівні ДО того, як почнемо додавати дітей

            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                // забираємо вузол з черги і записуємо його значення

                if (node.left != null) {
                    queue.offer(node.left);
                    // лівий нащадок потрапить у чергу і буде оброблений на наступному рівні
                }
                if (node.right != null) {
                    queue.offer(node.right);
                    // правий нащадок — так само
                }
            }

            result.add(level);
            // рівень повністю оброблено — зберігаємо його
        }

        return result;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        System.out.println(levelOrder(root)); // [[3], [9, 20], [15, 7]]
    }
}
