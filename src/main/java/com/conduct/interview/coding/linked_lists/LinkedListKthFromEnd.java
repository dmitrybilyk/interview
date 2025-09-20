package com.conduct.interview.coding.linked_lists;

public class LinkedListKthFromEnd {

    // Node definition
    static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    // LinkedList definition
    static class LinkedList {
        Node head;

        // Insert new node at the end
        void add(int data) {
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

        // Find middle element using slow/fast pointers
        int findMiddle() {
            if (head == null) {
                throw new RuntimeException("List is empty");
            }

            Node slow = head;
            Node fast = head;

            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
            }

            return slow.data;
        }

        // Detect if list has a loop (Floyd's cycle detection)
        boolean hasLoop() {
            Node slow = head;
            Node fast = head;

            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
                if (slow == fast) {
                    return true; // loop detected
                }
            }
            return false;
        }

        // Find k-th element from end
        int findKthFromEnd(int k) {
            if (head == null) {
                throw new RuntimeException("List is empty");
            }

            Node first = head;
            Node second = head;

            // Move first k steps ahead
            for (int i = 0; i < k; i++) {
                if (first == null) {
                    throw new IllegalArgumentException("k is larger than list size");
                }
                first = first.next;
            }

            // Move both pointers until first reaches the end
            while (first != null) {
                first = first.next;
                second = second.next;
            }

            return second.data;
        }

        // Print the list (unsafe if loop exists)
        void printList() {
            Node current = head;
            while (current != null) {
                System.out.print(current.data + " ");
                current = current.next;
            }
            System.out.println();
        }
    }

    // Main method to run
    public static void main(String[] args) {
        LinkedList list = new LinkedList();
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(40);
        list.add(50);

        System.out.print("Linked List: ");
        list.printList();

        System.out.println("Middle Element: " + list.findMiddle());
        System.out.println("Has Loop? " + list.hasLoop());
        System.out.println("2nd element from end: " + list.findKthFromEnd(2));
        System.out.println("5th element from end: " + list.findKthFromEnd(5));
    }
}
