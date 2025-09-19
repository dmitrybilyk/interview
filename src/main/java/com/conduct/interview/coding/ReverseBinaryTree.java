package com.conduct.interview.coding;

import java.util.LinkedList;
import java.util.Queue;

public class ReverseBinaryTree {

    public static void main(String[] args) {
        /*
                1
          /           \
        2               3
      /   \           /   \
     4     5         6     7
    / \   / \       / \   / \
   8  9 10 11     12 13 14 15
preorder(root);   //1 2 4 8 9 5 10 11 3 6 12 13 7 14 15
inorder(root);    // 8 4 9 2 10 5 11 1 12 6 13 3 14 7 15
postorder(root);  // 8 9 4 10 11 5 2 12 13 6 14 15 7 3 1
levelOrder(root); // 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
        */
        BinaryTreeNode root = new BinaryTreeNode(1);
        root.left = new BinaryTreeNode(2);
        root.right = new BinaryTreeNode(3);

        root.left.left = new BinaryTreeNode(4);
        root.left.right = new BinaryTreeNode(5);
        root.right.left = new BinaryTreeNode(6);
        root.right.right = new BinaryTreeNode(7);

        // Level 4
        root.left.left.left = new BinaryTreeNode(8);
        root.left.left.right = new BinaryTreeNode(9);
        root.left.right.left = new BinaryTreeNode(10);
        root.left.right.right = new BinaryTreeNode(11);
        root.right.left.left = new BinaryTreeNode(12);
        root.right.left.right = new BinaryTreeNode(13);
        root.right.right.left = new BinaryTreeNode(14);
        root.right.right.right = new BinaryTreeNode(15);

        System.out.println("Before reverse:");
        printTree(root);

        reverse(root);

        System.out.println("\nAfter reverse:");
        printTree(root);
    }


    public static void reverse(BinaryTreeNode root) {
        if (root == null) {
            return;
        }
        BinaryTreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;
        reverse(root.left);
        reverse(root.right);
    }

    // Prints the binary tree sideways (right first)
    public static void printTree(BinaryTreeNode root) {
        printTree(root, 0);
    }

    private static void printTree(BinaryTreeNode node, int level) {
        if (node == null) {
            return;
        }

        // First print right subtree
        printTree(node.right, level + 1);

        // Print current node after spacing
        for (int i = 0; i < level; i++) {
            System.out.print("    "); // 4 spaces per level
        }
        System.out.println(node.value);

        // Then print left subtree
        printTree(node.left, level + 1);
    }

}

class BinaryTreeNode {
    int value;
    BinaryTreeNode left, right;

    BinaryTreeNode(int value) {
        this.value = value;
        left = right = null;
    }
}


