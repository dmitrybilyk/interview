package com.conduct.interview.coding.leetcode._7_trees._2_dfs_problems;

import com.conduct.interview.coding.leetcode._7_trees.TreeNode;

public class SameTreeCheck {
    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);

        TreeNode anotherRoot = new TreeNode(1);
        anotherRoot.left = new TreeNode(2);
        anotherRoot.right = new TreeNode(3);

        System.out.println(compareTree(root, anotherRoot));
    }

    private static boolean compareTree(TreeNode root, TreeNode anotherRoot) {
        if (root == null && anotherRoot == null) {
            return true;
        }
        if (root == null || anotherRoot == null) {
            return false;
        }
        return root.val == anotherRoot.val &&
                compareTree(root.left, anotherRoot.left) &&
                compareTree(root.right, anotherRoot.right);
    }
}
