package com.conduct.interview._20_algorythms_and_data_structures.trees;

public class BSTInsert {

    // Definition for a binary tree node
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    // Recursive insert
    public TreeNode insert(TreeNode root, int val) {
        if (root == null) return new TreeNode(val);
        if (val < root.val) root.left = insert(root.left, val);
        else if (val > root.val) root.right = insert(root.right, val);
        return root; // duplicates ignored
    }

    // Test run
    public static void main(String[] args) {
        BSTInsert bst = new BSTInsert();
        TreeNode root = null;
        int[] vals = {8, 3, 10, 1, 6, 14, 4, 7, 13};
        for (int v : vals) root = bst.insert(root, v);

        inorder(root); // Output: 1 3 4 6 7 8 10 13 14
    }

    // Simple inorder traversal to check tree
    static void inorder(TreeNode node) {
        if (node == null) return;
        inorder(node.left);
        System.out.print(node.val + " ");
        inorder(node.right);
    }
}
