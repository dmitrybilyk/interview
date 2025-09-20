package com.conduct.interview.coding.trees.traversal;

import java.util.LinkedList;
import java.util.Queue;

public class TreesTraversalCheck {
    public static void main(String[] args) {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(5);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(30);
        root.left.right = new TreeNode(33);
        root.right.left = new TreeNode(4);
        root.right.right = new TreeNode(2);

        printPreorder(root);
        printInorder(root);
        printPostorder(root);
        levelOrder(root);
    }

    private static void printPreorder(TreeNode root) {
        if (root == null) {
            return;
        }
        System.out.println(root.value);
        printPreorder(root.left);
        printPreorder(root.right);
    }

    private static void printInorder(TreeNode root) {
        if (root == null) {
            return;
        }
        printInorder(root.left);
        System.out.println(root.value);
        printInorder(root.right);
    }

    private static void printPostorder(TreeNode root) {
        if (root == null) {
            return;
        }
        printPostorder(root.left);
        printPostorder(root.right);
        System.out.println(root.value);
    }

    // Breadth-First (Level Order)
    public static void levelOrder(TreeNode root) {
        if (root == null) return;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            System.out.print(node.value + " ");
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
    }
}

class TreeNode {
    int value;
    TreeNode left, right = null;

    public TreeNode(int value) {
        this.value = value;
    }
}