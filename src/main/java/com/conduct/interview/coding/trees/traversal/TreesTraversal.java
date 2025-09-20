package com.conduct.interview.coding.trees.traversal;

import java.util.LinkedList;
import java.util.Queue;

public class TreesTraversal {

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
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);

        root.left.left = new Node(4);
        root.left.right = new Node(5);
        root.right.left = new Node(6);
        root.right.right = new Node(7);

        // Level 4
        root.left.left.left = new Node(8);
        root.left.left.right = new Node(9);
        root.left.right.left = new Node(10);
        root.left.right.right = new Node(11);
        root.right.left.left = new Node(12);
        root.right.left.right = new Node(13);
        root.right.right.left = new Node(14);
        root.right.right.right = new Node(15);

        System.out.print("Preorder: ");
        preorder(root);   //1 2 4 8 9 5 10 11 3 6 12 13 7 14 15
        System.out.println();

        System.out.print("Inorder: ");
        inorder(root);    // 8 4 9 2 10 5 11 1 12 6 13 3 14 7 15
        System.out.println();

        System.out.print("Postorder: ");
        postorder(root);  // 8 9 4 10 11 5 2 12 13 6 14 15 7 3 1
        System.out.println();

        System.out.print("Level-order (Breadth-first):  ");
        levelOrder(root); // 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
    }


    // Preorder: Root -> Left -> Right
    public static void preorder(Node root) {
        if (root == null) return;
        System.out.print(root.value + " ");
        preorder(root.left);
        preorder(root.right);
    }

    // Inorder: Left -> Root -> Right
    public static void inorder(Node root) {
        if (root == null) return;
        inorder(root.left);
        System.out.print(root.value + " ");
        inorder(root.right);
    }

    // Postorder: Left -> Right -> Root
    public static void postorder(Node root) {
        if (root == null) return;
        postorder(root.left);
        postorder(root.right);
        System.out.print(root.value + " ");
    }

    // Breadth-First (Level Order)
    public static void levelOrder(Node root) {
        if (root == null) return;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            System.out.print(node.value + " ");
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
    }
}

class Node {
    int value;
    Node left, right;

    Node(int value) {
        this.value = value;
        left = right = null;
    }
}
