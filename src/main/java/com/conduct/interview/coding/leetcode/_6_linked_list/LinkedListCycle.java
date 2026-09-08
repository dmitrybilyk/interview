package com.conduct.interview.coding.leetcode._6_linked_list;

// LC 141 — Linked List Cycle
// Pattern: Floyd's slow/fast pointers — if they meet, there's a cycle
// Time O(n), Space O(1)

// Задача: визначити, чи є цикл у зв'язному списку.
// Ідея "черепаха і заєць" (Floyd's cycle detection):
// два вказівники йдуть з різною швидкістю; якщо циклу немає — fast дійде до кінця;
// якщо цикл є — рано чи пізно fast наздожене slow всередині циклу.
public class LinkedListCycle {

    static class Node { int val; Node next; Node(int v) { val = v; } }

    public static boolean hasCycle(Node head) {
        Node slow = head, fast = head;
        // slow рухається на 1 крок за ітерацію, fast — на 2 кроки

        while (fast != null && fast.next != null) {
            // перевіряємо fast.next, бо fast завжди йде на два вузли — інакше NPE

            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
            // вказівники зустрілись у тій самій ноді -> це можливо тільки всередині циклу
        }
        return false;
        // fast дійшов до кінця (null) -> циклу немає
    }

    public static void main(String[] args) {
        Node a = new Node(1), b = new Node(2), c = new Node(3);
        a.next = b; b.next = c;
        System.out.println(hasCycle(a)); // false
        c.next = b;                      // create cycle
        System.out.println(hasCycle(a)); // true
    }
}
