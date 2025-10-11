package com.conduct.interview.coding.linked_lists.sll;

// Definition of a Node for the linked list
class Node {
    int data;
    Node next;

    Node(int data) {
        this.data = data;
        this.next = null;
    }
}

class LinkedList {
    Node head;

    // Method to get the n-th element (0-based index)
    public int getNthElement(int n) {
        // Check if the list is empty
        if (head == null) {
            return -1;
        }

        Node current = head;
        int count = 0;

        // Traverse the list until the n-th node or end is reached
        while (current != null) {
            if (count == n) {
                return current.data;
            }
            count++;
            current = current.next;
        }

        // If n is greater than or equal to the list length
        return -1;
    }

    // Utility method to add a node at the end of the list
    public void append(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            return;
        }
        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = newNode;
    }

    // Utility method to print the list (for testing)
    public void printList() {
        Node current = head;
        while (current != null) {
            System.out.print(current.data + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }

    public static void main(String[] args) {
        LinkedList list = new LinkedList();
        list.append(1);
        list.append(2);
        list.append(3);
        list.append(4);

        System.out.println("Linked List: ");
        list.printList();

        // Test cases
        System.out.println("0th element: " + list.getNthElement(0)); // Should print 1
        System.out.println("2nd element: " + list.getNthElement(2)); // Should print 3
        System.out.println("4th element: " + list.getNthElement(4)); // Should print -1 (out of bounds)
        System.out.println("-1th element: " + list.getNthElement(-1)); // Should print -1 (invalid index)
    }
}