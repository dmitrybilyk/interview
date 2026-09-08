package com.conduct.interview.coding.leetcode;

// LC 104 — Maximum Depth of Binary Tree
// Pattern: DFS — depth = 1 + max(left depth, right depth)
// Time O(n), Space O(h)
public class MaxDepthBinaryTree {

    static class TreeNode { int val; TreeNode left, right; TreeNode(int v) { val = v; } }

    public static int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
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
