package com.conduct.interview.coding.leetcode._7_trees._2_dfs_problems;

import com.conduct.interview.coding.leetcode._7_trees.TreeNode;

// LC 100 — Same Tree
// Pattern: DFS — compare both trees node by node in parallel
// Time O(n), Space O(h) — h це висота дерева (глибина рекурсії)

// Задача: перевірити, чи два бінарні дерева ідентичні за формою і значеннями.
// Ідея: два дерева однакові, якщо значення в коренях збігаються і РЕКУРСИВНО
// однакові обидва ліві піддерева та обидва праві.
public class SameTree {

    public static boolean isSameTree(TreeNode root, TreeNode anotherRoot) {
        if (root == null && anotherRoot == null) {
            return true;
            // обидва піддерева порожні одночасно — на цій гілці все збігається
        }
        if (root == null || anotherRoot == null) {
            return false;
            // тільки ОДНЕ з двох порожнє -> дерева вже точно різні за формою
        }

        return root.val == anotherRoot.val
                && isSameTree(root.left, anotherRoot.left)
                && isSameTree(root.right, anotherRoot.right);
        // значення в поточних вузлах мають збігатись, і РЕКУРСИВНО
        // обидва ліві піддерева мають бути однакові, і обидва праві теж
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);

        TreeNode anotherRoot = new TreeNode(1);
        anotherRoot.left = new TreeNode(2);
        anotherRoot.right = new TreeNode(3);
        // друге дерево з такою самою формою і значеннями -> має вважатись "тим самим"

        System.out.println(isSameTree(root, anotherRoot)); // true

        anotherRoot.right.val = 4;
        System.out.println(isSameTree(root, anotherRoot)); // false
    }
}
