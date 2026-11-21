package com.conduct.interview.coding.leetcode._7_trees._2_dfs_problems;

import com.conduct.interview.coding.leetcode._7_trees.TreeNode;

// LC 226 — Invert Binary Tree
// Pattern: Post-order DFS — swap children bottom-up
// Time O(n), Space O(h) stack

// Задача: дзеркально відобразити бінарне дерево (поміняти ліве й праве піддерево місцями,
// причому на КОЖНОМУ рівні, а не тільки в корені).
// Ідея: рекурсивно інвертуємо ліве й праве піддерева, а потім міняємо їх місцями в поточному вузлі.
public class InvertBinaryTreeCheck {

    public static TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }

        TreeNode tmp = root.left;

        root.left = invertTree(root.right);

        root.right = invertTree(tmp);

        return root;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(4);
        root.left  = new TreeNode(2); root.right = new TreeNode(7);
        root.left.left  = new TreeNode(1); root.left.right  = new TreeNode(3);
        root.right.left = new TreeNode(6); root.right.right = new TreeNode(9);
        System.out.println("Before:");
        printTree(root);
        // друкуємо ДО інверсії — invertTree змінює дерево на місці

        TreeNode inv = invertTree(root);

        System.out.println("After:");
        printTree(inv);
    }

    // Друкує дерево ієрархічно: кожен рівень зсунутий вправо, L/R позначають лівого/правого нащадка
    public static void printTree(TreeNode root) {
        if (root == null) {
            System.out.println("(empty)");
            return;
        }
        System.out.println(root.val);
        printChildren(root, "");
    }

    private static void printChildren(TreeNode node, String prefix) {
        if (node.left == null && node.right == null) {
            return;
            // листок — нащадків не друкуємо
        }
        printBranch(node.left, prefix, "L", false);
        printBranch(node.right, prefix, "R", true);
    }

    private static void printBranch(TreeNode node, String prefix, String side, boolean isLast) {
        System.out.println(prefix + (isLast ? "└── " : "├── ") + side + ": " + (node == null ? "null" : node.val));
        if (node != null) {
            printChildren(node, prefix + (isLast ? "    " : "│   "));
            // для останнього нащадка вертикальна лінія далі не потрібна
        }
    }
}
