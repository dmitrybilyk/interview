package com.conduct.interview.coding.linked_lists;

public class LinkedListLoopCheck {

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

        // Detect if list has a loop
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
            return false; // no loop
        }

        // Print the list (WARNING: won't stop if loop exists!)
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

        // Create a loop manually: connect last node to second node
        list.head.next.next.next.next.next = list.head.next;

        System.out.println("Has Loop after modification? " + list.hasLoop());
    }
}
