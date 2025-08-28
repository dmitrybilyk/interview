package com.conduct.interview._20_algorythms_and_data_structures.trees;

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}

public class BSTValidator {

    public boolean isValidBST(TreeNode root) {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean validate(TreeNode node, long min, long max) {
        if (node == null) return true;
        if (node.val <= min || node.val >= max) return false;

        return validate(node.left, min, node.val)
            && validate(node.right, node.val, max);
    }

    // Small test
    public static void main(String[] args) {
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(3);
        root.right = new TreeNode(7);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);

        BSTValidator validator = new BSTValidator();
        System.out.println(validator.isValidBST(root)); // true

        root.left.right.val = 6; // break the rule
        System.out.println(validator.isValidBST(root)); // false
    }
}
