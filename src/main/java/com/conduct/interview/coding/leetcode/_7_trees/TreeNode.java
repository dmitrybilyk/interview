package com.conduct.interview.coding.leetcode._7_trees;

// LC 100 — Same Tree
// Цей клас поєднує дві речі:
// 1) саму структуру вузла бінарного дерева (поля val/left/right) — типовий "будівельний блок" LeetCode;
// 2) алгоритм compareTrees, що перевіряє, чи два дерева ідентичні за формою і значеннями.
// Час O(n), Пам'ять O(h) — h це висота дерева (глибина рекурсії)
public class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int x) {
        val = x;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);

        TreeNode anotherRoot = new TreeNode(1);
        anotherRoot.left = new TreeNode(2);
        anotherRoot.right = new TreeNode(3);
        // друге дерево з такою самою формою і значеннями -> має вважатись "тим самим"

        System.out.println(compareTrees(root, anotherRoot));
    }

    private static boolean compareTrees(TreeNode root, TreeNode anotherRoot) {
        if (root == null && anotherRoot == null) {
            return true;
            // обидва піддерева порожні одночасно — на цій гілці все збігається
        }
        if (root == null || anotherRoot == null) {
            return false;
            // тільки ОДНЕ з двох порожнє -> дерева вже точно різні за формою
        }

        return root.val == anotherRoot.val
                && compareTrees(root.left, anotherRoot.left)
                && compareTrees(root.right, anotherRoot.right);
        // значення в поточних вузлах мають збігатись, і РЕКУРСИВНО
        // обидва ліві піддерева мають бути однакові, і обидва праві теж
    }
}
