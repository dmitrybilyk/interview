package com.conduct.interview._20_algorythms_and_data_structures.linked_lists.partition_list;

public class PartitionListExample {

    // ----------------------------
    // Definition for singly-linked list.
    // ----------------------------
    static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    // ----------------------------
    // Head pointer for the linked list
    // ----------------------------
    private Node head;

    // ----------------------------
    // Partition function
    // ----------------------------
    public void partitionList(int x) {
        // Step 1: Check for an empty list.
        if (head == null) return;

        // Step 2: Create two dummy nodes (for < x and >= x lists).
        Node dummy1 = new Node(0);
        Node dummy2 = new Node(0);

        // Step 3: Pointers to build the two lists.
        Node prev1 = dummy1;
        Node prev2 = dummy2;

        // Step 4: Traverse the original list.
        Node current = head;

        while (current != null) {
            if (current.value < x) {
                // Belongs to the "smaller" list
                prev1.next = current;
                prev1 = current;
            } else {
                // Belongs to the "greater/equal" list
                prev2.next = current;
                prev2 = current;
            }
            current = current.next;
        }

// Step 5: Close the greater/equal list
        prev2.next = null;

// Step 6: Connect smaller list to greater/equal list
        prev1.next = dummy2.next;

// Step 7: Update head
        head = dummy1.next;
    }

    // ----------------------------
    // Helper: insert at end
    // ----------------------------
    public void addNode(int value) {
        if (head == null) {
            head = new Node(value);
            return;
        }
        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = new Node(value);
    }

    // ----------------------------
    // Helper: print list
    // ----------------------------
    public void printList() {
        Node current = head;
        while (current != null) {
            System.out.print(current.value + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }

    // ----------------------------
    // Main: test the partition logic
    // ----------------------------
    public static void main(String[] args) {
        PartitionListExample list = new PartitionListExample();
        list.addNode(1);
        list.addNode(4);
        list.addNode(3);
        list.addNode(2);
        list.addNode(5);
        list.addNode(2);

        System.out.println("Original list:");
        list.printList();

        int x = 3;
        list.partitionList(x);

        System.out.println("\nPartitioned list around x = " + x + ":");
        list.printList();
    }
}
