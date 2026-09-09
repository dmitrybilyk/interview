package com.conduct.interview.coding.leetcode._7_trees;

// LC 226 — Invert Binary Tree
// Pattern: Post-order DFS — swap children bottom-up
// Time O(n), Space O(h) stack

// Задача: дзеркально відобразити бінарне дерево (поміняти ліве й праве піддерево місцями,
// причому на КОЖНОМУ рівні, а не тільки в корені).
// Ідея: рекурсивно інвертуємо ліве й праве піддерева, а потім міняємо їх місцями в поточному вузлі.
public class InvertBinaryTree {

    static class TreeNode { int val; TreeNode left, right; TreeNode(int v) { val = v; } }

    public static TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
            // базовий випадок рекурсії — порожнє піддерево інвертувати нічого
        }

        TreeNode tmp = root.left;
        // зберігаємо ліве піддерево, бо зараз перезапишемо root.left

        root.left  = invertTree(root.right);
        // нове ліве піддерево — це інвертоване ПРАВЕ

        root.right = invertTree(tmp);
        // нове праве піддерево — це інвертоване (старе) ЛІВЕ, яке ми зберегли в tmp

        return root;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(4);
        root.left  = new TreeNode(2); root.right = new TreeNode(7);
        root.left.left  = new TreeNode(1); root.left.right  = new TreeNode(3);
        root.right.left = new TreeNode(6); root.right.right = new TreeNode(9);
        TreeNode inv = invertTree(root);
        // root should now be 4 → right:2, left:7
        System.out.println(inv.left.val);  // 7
        System.out.println(inv.right.val); // 2
    }
}
