package com.conduct.interview.coding.leetcode;

// LC 226 — Invert Binary Tree
// Pattern: Post-order DFS — swap children bottom-up
// Time O(n), Space O(h) stack
public class InvertBinaryTree {

    static class TreeNode { int val; TreeNode left, right; TreeNode(int v) { val = v; } }

    public static TreeNode invertTree(TreeNode root) {
        if (root == null) return null;
        TreeNode tmp = root.left;
        root.left  = invertTree(root.right);
        root.right = invertTree(tmp);
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
