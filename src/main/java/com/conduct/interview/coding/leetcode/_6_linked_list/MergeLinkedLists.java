package com.conduct.interview.coding.leetcode._6_linked_list;

// LC 21 — Merge Two Sorted Lists
// Задача: об'єднати два ВІДСОРТОВАНІ зв'язні списки в один відсортований список.
// Ідея: dummy-вузол як "заглушка" для старту, далі завжди приєднуємо
// вузол з меншим значенням і рухаємо відповідний вказівник вперед.
// Час O(n + m), Пам'ять O(1) (переставляємо існуючі вузли, нових не створюємо)
public class MergeLinkedLists {
    public static void main(String[] args) {
        ListNode l1 = new ListNode(1);
        ListNode l2 = new ListNode(2);
        ListNode l3 = new ListNode(4);
        // перший список: 1 -> 2 -> 4

        ListNode l4 = new ListNode(1);
        ListNode l5 = new ListNode(3);
        ListNode l6 = new ListNode(4);
        // другий список: 1 -> 3 -> 4

        l1.next = l2;
        l2.next = l3;

        l4.next = l5;
        l5.next = l6;

        mergeTwoLists(l1, l2);
        // УВАГА: тут, схоже, помилка — передано (l1, l2), тобто голову першого списку
        // і другий вузол ТОГО Ж списку. Мало б бути mergeTwoLists(l1, l4),
        // щоб об'єднати саме два різні списки.
        System.out.println("dfd");
        // результат злиття тут навіть не виводиться — println друкує просто константний рядок
    }

    private static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        // фіктивний вузол-заглушка, щоб не обробляти окремо випадок "перший вузол результату"

        ListNode curr = dummy;
        // curr — вказівник на останній доданий вузол результату

        while(l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                curr.next = l1;
                l1 = l1.next;
                // менший вузол зараз у l1 -> приєднуємо його і рухаємо l1 далі
            } else {
                curr.next = l2;
                l2 = l2.next;
                // менший вузол зараз у l2 -> приєднуємо його і рухаємо l2 далі
            }
            curr = curr.next;
            // пересуваємо "хвіст" результату на щойно доданий вузол
        }

        curr.next = l1 == null ? l2 : l1;
        // один зі списків уже закінчився -> приєднуємо залишок другого списку одразу цілком
        // (він і так відсортований)

        return dummy.next;
        // dummy сам по собі не частина результату — справжня голова в dummy.next
    }
}

class ListNode {
    int val;
    ListNode next;
    ListNode(int x) {
        val = x;
    }
}
