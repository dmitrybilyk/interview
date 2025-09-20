package com.conduct.interview.coding.linked_lists;

public class DoublyLinkedListReverse {

    // Doubly linked list node
    static class Node {
        int data;
        Node prev, next;

        Node(int data) {
            this.data = data;
        }
    }

    // Doubly linked list
    static class DoublyLinkedList {
        Node head, tail;

        // Insert at the end
        void append(int data) {
            Node newNode = new Node(data);
            if (head == null) {
                head = tail = newNode;
                return;
            }
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        // Reverse the list in-place
        void reverse() {
            Node current = head;
            Node temp = null;

            while (current != null) {
                // swap prev and next
                temp = current.prev;
                current.prev = current.next;
                current.next = temp;

                // move forward (actually to prev, since swapped)
                current = current.prev;
            }

            // swap head and tail
            temp = head;
            head = tail;
            tail = temp;
        }

        // Print list forward
        void printForward() {
            Node curr = head;
            while (curr != null) {
                System.out.print(curr.data + " ");
                curr = curr.next;
            }
            System.out.println();
        }

        // Print list backward
        void printBackward() {
            Node curr = tail;
            while (curr != null) {
                System.out.print(curr.data + " ");
                curr = curr.prev;
            }
            System.out.println();
        }
    }

    // Main to test
    public static void main(String[] args) {
        DoublyLinkedList dll = new DoublyLinkedList();

        dll.append(10);
        dll.append(20);
        dll.append(30);
        dll.append(40);

        System.out.println("Original list forward:");
        dll.printForward();

        System.out.println("Original list backward:");
        dll.printBackward();

        dll.reverse();

        System.out.println("Reversed list forward:");
        dll.printForward();

        System.out.println("Reversed list backward:");
        dll.printBackward();
    }
}
