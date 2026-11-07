package com.conduct.interview.coding.leetcode._6_linked_list;

// LC 206 — Reverse Linked List
// Pattern: Iterative pointer reversal (3-pointer trick)
// Time O(n), Space O(1)

// Задача: розвернути зв'язний список так, щоб останній вузол став першим.
// Ідея "3 вказівники": на кожному кроці зберігаємо next ДО того, як перезапишемо
// curr.next, інакше втратимо решту списку.
public class ReverseLinkedListCheck {

    static class Node {
        int val; Node next;
        Node(int v) { val = v; }
    }

    public static Node reverse(Node head) {
        Node prev = null, curr = head;

        while(curr != null) {
            Node next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        return prev;
    }

    public static void main(String[] args) {
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        Node r = reverse(head);
        while (r != null) { System.out.print(r.val + " "); r = r.next; } // 4 3 2 1
    }
}
