package com.conduct.interview.coding.leetcode._6_linked_list;

// LC 141 — Linked List Cycle
// Pattern: Floyd's slow/fast pointers — if they meet, there's a cycle
// Time O(n), Space O(1)

// Задача: визначити, чи є цикл у зв'язному списку.
// Ідея "черепаха і заєць" (Floyd's cycle detection):
// два вказівники йдуть з різною швидкістю; якщо циклу немає — fast дійде до кінця;
// якщо цикл є — рано чи пізно fast наздожене slow всередині циклу.
public class LinkedListCycleCheck2 {

    static class Node {
        int val;
        Node next;
        public Node(int v) {
            this.val = v;
        }
    }

    public static void main(String[] args) {
        Node first = new Node(1);
        Node second = new Node(2);
        Node third = new Node(3);

        first.next = second;
        second.next = third;
        third.next = first;

        System.out.println(checkIfCycle(first));
    }

    private static boolean checkIfCycle(Node head) {
        Node slow = head, fast = head;

        while(fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                return true;
            }
        }
        return false;
    }
}
