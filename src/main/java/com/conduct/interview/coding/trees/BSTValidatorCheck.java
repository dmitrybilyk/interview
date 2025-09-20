package com.conduct.interview.coding.trees;

public class BSTValidatorCheck {

    public static void main(String[] args) {
        BTreeNode bTreeNode = new BTreeNode(7);
        bTreeNode.left = new BTreeNode(4);
        bTreeNode.right = new BTreeNode(9);
        bTreeNode.left.left = new BTreeNode(3);
        bTreeNode.left.right = new BTreeNode(6);
        bTreeNode.right.left = new BTreeNode(8);
        bTreeNode.right.right = new BTreeNode(10);

        System.out.println(checkIfTreeIsBinarySearchTree(bTreeNode));
    }

    private static boolean checkIfTreeIsBinarySearchTree(BTreeNode bTreeNode) {
        return validateNode(bTreeNode, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static boolean validateNode(BTreeNode node, int min, int max) {
        if (node == null) return true;
        if (node.data <= min || node.data >= max) return false;
        return validateNode(node.left, min, node.data) &&
                validateNode(node.right, node.data, max);
    }

}

class BTreeNode {
    int data;
    BTreeNode left;
    BTreeNode right;

    public BTreeNode(int data) {
        this.data = data;
    }
}
