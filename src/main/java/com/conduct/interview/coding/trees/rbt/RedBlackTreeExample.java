package com.conduct.interview.coding.trees.rbt;

class Node {
    int data;
    Node left, right, parent;
    boolean isRed;

    Node(int data) {
        this.data = data;
        this.isRed = true; // new nodes are red by default
    }
}

class RedBlackTree {
    private Node root;

    // Helper to print tree structure
    public void printTree(Node node, int indent) {
        if (node == null) return;

        printTree(node.right, indent + 4);

        if (indent != 0)
            System.out.print(" ".repeat(indent));
        System.out.println((node.isRed ? "R" : "B") + "(" + node.data + ")");

        printTree(node.left, indent + 4);
    }

    public void insert(int data) {
        Node newNode = bstInsert(root, new Node(data));
        fixViolations(newNode);
    }

    private Node bstInsert(Node root, Node node) {
        if (root == null) {
            if (this.root == null)
                this.root = node;
            return node;
        }

        if (node.data < root.data) {
            root.left = bstInsert(root.left, node);
            root.left.parent = root;
        } else if (node.data > root.data) {
            root.right = bstInsert(root.right, node);
            root.right.parent = root;
        }

        return root;
    }

    private void fixViolations(Node node) {
        Node parent = null, grandParent = null;

        while (node != root && node.isRed && node.parent.isRed) {
            parent = node.parent;
            grandParent = parent.parent;

            // Parent is left child of grandparent
            if (parent == grandParent.left) {
                Node uncle = grandParent.right;

                // Case 1: Uncle is red — recolor
                if (uncle != null && uncle.isRed) {
                    grandParent.isRed = true;
                    parent.isRed = false;
                    uncle.isRed = false;
                    node = grandParent;
                } else {
                    // Case 2: node is right child — rotate left
                    if (node == parent.right) {
                        rotateLeft(parent);
                        node = parent;
                        parent = node.parent;
                    }

                    // Case 3: node is left child — rotate right
                    rotateRight(grandParent);
                    boolean tmpColor = parent.isRed;
                    parent.isRed = grandParent.isRed;
                    grandParent.isRed = tmpColor;
                    node = parent;
                }
            }
            // Parent is right child of grandparent
            else {
                Node uncle = grandParent.left;

                if (uncle != null && uncle.isRed) {
                    grandParent.isRed = true;
                    parent.isRed = false;
                    uncle.isRed = false;
                    node = grandParent;
                } else {
                    if (node == parent.left) {
                        rotateRight(parent);
                        node = parent;
                        parent = node.parent;
                    }

                    rotateLeft(grandParent);
                    boolean tmpColor = parent.isRed;
                    parent.isRed = grandParent.isRed;
                    grandParent.isRed = tmpColor;
                    node = parent;
                }
            }
        }

        root.isRed = false; // root always black
    }

    private void rotateLeft(Node node) {
        Node rightChild = node.right;
        node.right = rightChild.left;
        if (node.right != null)
            node.right.parent = node;

        rightChild.parent = node.parent;
        if (node.parent == null)
            root = rightChild;
        else if (node == node.parent.left)
            node.parent.left = rightChild;
        else
            node.parent.right = rightChild;

        rightChild.left = node;
        node.parent = rightChild;
    }

    private void rotateRight(Node node) {
        Node leftChild = node.left;
        node.left = leftChild.right;
        if (node.left != null)
            node.left.parent = node;

        leftChild.parent = node.parent;
        if (node.parent == null)
            root = leftChild;
        else if (node == node.parent.left)
            node.parent.left = leftChild;
        else
            node.parent.right = leftChild;

        leftChild.right = node;
        node.parent = leftChild;
    }

    public Node getRoot() {
        return root;
    }
}

public class RedBlackTreeExample {
    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();

        tree.insert(10);
        tree.insert(20);
        tree.insert(30);

        System.out.println("Red-Black Tree structure after inserts:");
        tree.printTree(tree.getRoot(), 0);
    }
}