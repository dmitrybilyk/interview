package com.conduct.interview.leetcode;

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

        System.out.println(compareTrees(root, anotherRoot));
    }

    private static boolean compareTrees(TreeNode root, TreeNode anotherRoot) {
        if (root == null && anotherRoot == null) {
            return true;
        }
        if (root == null || anotherRoot == null) {
            return false;
        }

        return root.val == anotherRoot.val
                && compareTrees(root.left, anotherRoot.left)
                && compareTrees(root.right, anotherRoot.right);
    }
}
