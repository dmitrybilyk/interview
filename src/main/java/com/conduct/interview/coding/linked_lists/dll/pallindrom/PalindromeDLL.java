package com.conduct.interview.coding.linked_lists.dll.pallindrom;

public class PalindromeDLL {
    // Node definition
    static class Node {
        int val;
        Node prev, next;
        Node(int val) {
            this.val = val;
        }
    }

    // Check palindrome
    public static boolean isPalindrome(Node head) {
        if (head == null || head.next == null) return true;

        // Find tail & length
        Node tail = head;
        int length = 1;
        while (tail.next != null) {
            tail = tail.next;
            length++;
        }

        // Compare first half and last half
        Node left = head;
        Node right = tail;
        for (int i = 0; i < length / 2; i++) {
            if (left.val != right.val) return false;
            left = left.next;
            right = right.prev;
        }
        return true;
    }

    // Demo
    public static void main(String[] args) {
        // Create palindrome: 1 <-> 2 <-> 3 <-> 2 <-> 1
        Node head = new Node(1);
        head.next = new Node(2); head.next.prev = head;
        head.next.next = new Node(3); head.next.next.prev = head.next;
        head.next.next.next = new Node(2); head.next.next.next.prev = head.next.next;
        head.next.next.next.next = new Node(1); head.next.next.next.next.prev = head.next.next.next;

        System.out.println(isPalindrome(head)); // true
    }
}
