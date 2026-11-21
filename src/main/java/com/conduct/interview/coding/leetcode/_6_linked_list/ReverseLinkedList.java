package com.conduct.interview.coding.leetcode._6_linked_list;

// LC 206 — Reverse Linked List
// Pattern: Iterative pointer reversal (3-pointer trick)
// Time O(n), Space O(1)

// Задача: розвернути зв'язний список так, щоб останній вузол став першим.
// Ідея "3 вказівники": на кожному кроці зберігаємо next ДО того, як перезапишемо
// curr.next, інакше втратимо решту списку.

//Люди в черзі
//
//Троє людей стоять у ряд, і кожен вказує пальцем на наступного:
//
//        👤1 👉 👤2 👉 👤3 👉 (стіна)
//
//Завдання: щоб кожен вказував на попереднього, а не на наступного.
//
//Ти йдеш уздовж черги й підходиш до кожного по черзі. Біля кожної людини робиш те саме:
//
//        1. Дивишся, на кого вона зараз вказує, і запам'ятовуєш: «далі мені туди». Це next.
//        2. Повертаєш її палець назад, на людину, від якої ти щойно прийшов. Це curr.next = prev.
//  3. Робиш крок вперед. Людина, яку ти щойно розвернув, тепер позаду тебе (prev = curr), і ти йдеш до того, кого запам'ятав (curr = next).
//
//Чому пункт 1 обов'язковий: коли ти повернув палець людини 1 назад, вона більше не показує, де стоїть людина 2. Якщо не запам'ятав заздалегідь, далі йти нема куди.
//
//Коли дійшов до стіни (curr == null), остання людина, яку ти розвернув, — це нова голова. Її й повертаємо (return prev).

public class ReverseLinkedList {

    static class Node {
        int val; Node next;
        Node(int v) { val = v; }
    }

    public static Node reverse(Node head) {
        Node prev = null, curr = head;
        // prev — те, що вже розвернули; curr — вузол, який обробляємо зараз

        while (curr != null) {
            Node next = curr.next;
            // спочатку запам'ятовуємо "наступний", бо зараз перепишемо curr.next

            curr.next = prev;
            // розвертаємо стрілку: curr тепер вказує назад, на prev

            prev = curr;
            curr = next;
            // зсуваємо обидва вказівники на крок вперед по вихідному списку
        }
        return prev;
        // коли curr став null, prev вказує на нову голову (колишній останній вузол)
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
