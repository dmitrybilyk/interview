package com.conduct.interview.coding.trees.bst;

public class BSTContains {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    // Contains (iterative version)
    public boolean contains(TreeNode root, int val) {
        while (root != null) {
            if (val == root.val) return true;
            root = (val < root.val) ? root.left : root.right;
        }
        return false;
    }

    public static void main(String[] args) {
        // Create sample tree manually
        TreeNode root = new TreeNode(8);
        root.left = new TreeNode(3);
        root.right = new TreeNode(10);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(6);
        root.right.right = new TreeNode(14);

        BSTContains bst = new BSTContains();
        System.out.println(bst.contains(root, 6)); // true
        System.out.println(bst.contains(root, 7)); // false
    }
}
