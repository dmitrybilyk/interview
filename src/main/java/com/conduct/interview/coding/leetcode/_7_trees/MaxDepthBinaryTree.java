package com.conduct.interview.coding.leetcode._7_trees;

// LC 104 — Maximum Depth of Binary Tree
// Pattern: DFS — depth = 1 + max(left depth, right depth)
// Time O(n), Space O(h)

// Задача: знайти максимальну глибину (кількість рівнів) бінарного дерева.
// Ідея: глибина вузла = 1 (сам вузол) + глибина найглибшого з його піддерев.
public class MaxDepthBinaryTree {

    static class TreeNode { int val; TreeNode left, right; TreeNode(int v) { val = v; } }

    public static int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
            // порожнє дерево -> глибина 0 (базовий випадок рекурсії)
        }

        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
        // беремо глибшу з двох гілок і додаємо 1 за поточний рівень (сам root)
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left  = new TreeNode(15);
        root.right.right = new TreeNode(7);
        System.out.println(maxDepth(root)); // 3
    }
}
