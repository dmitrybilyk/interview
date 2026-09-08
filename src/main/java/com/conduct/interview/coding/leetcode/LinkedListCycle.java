package com.conduct.interview.coding.leetcode;

// LC 141 — Linked List Cycle
// Pattern: Floyd's slow/fast pointers — if they meet, there's a cycle
// Time O(n), Space O(1)
public class LinkedListCycle {

    static class Node { int val; Node next; Node(int v) { val = v; } }

    public static boolean hasCycle(Node head) {
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Node a = new Node(1), b = new Node(2), c = new Node(3);
        a.next = b; b.next = c;
        System.out.println(hasCycle(a)); // false
        c.next = b;                      // create cycle
        System.out.println(hasCycle(a)); // true
    }
}
