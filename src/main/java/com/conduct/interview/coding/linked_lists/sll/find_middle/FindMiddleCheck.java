package com.conduct.interview.coding.linked_lists.sll.find_middle;

public class FindMiddleCheck {
    public static void main(String[] args) {
        LinedList list = new LinedList();
        Node node = new Node(3);
        node.next = new Node(5);
        node.next.next = new Node(7);
        node.next.next.next = new Node(9);
        node.next.next.next.next = new Node(8);
        node.next.next.next.next.next = new Node(1);
        node.next.next.next.next.next.next = new Node(10);
        list.head = node;
//        list.printAll();
        System.out.println(list.findMiddleData());
    }

    static class LinedList {
        Node head;

        int findMiddleData() {
            Node slow = head;
            Node fast = head;

            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
            }
            return slow.data;
        }

        public void printAll() {
            Node current = head;

            while (current != null) {
                System.out.println(current.data + " ");
                current = current.next;
            }
        }
    }

    static class Node {
        int data;
        Node next;

        public Node(int data) {
            this.data = data;
        }
    }
}
