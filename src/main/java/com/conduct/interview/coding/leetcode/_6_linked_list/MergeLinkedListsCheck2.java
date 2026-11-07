package com.conduct.interview.coding.leetcode._6_linked_list;

// LC 21 — Merge Two Sorted Lists
// Задача: об'єднати два ВІДСОРТОВАНІ зв'язні списки в один відсортований список.
// Ідея: dummy-вузол як "заглушка" для старту, далі завжди приєднуємо
// вузол з меншим значенням і рухаємо відповідний вказівник вперед.
// Час O(n + m), Пам'ять O(1) (переставляємо існуючі вузли, нових не створюємо)
public class MergeLinkedListsCheck2 {
    public static void main(String[] args) {
        ListNode3 l1 = new ListNode3(1);
        ListNode3 l2 = new ListNode3(2);
        ListNode3 l3 = new ListNode3(4);
        // перший список: 1 -> 2 -> 4

        ListNode3 l4 = new ListNode3(1);
        ListNode3 l5 = new ListNode3(3);
        ListNode3 l6 = new ListNode3(4);
        // другий список: 1 -> 3 -> 4

        l1.next = l2;
        l2.next = l3;

        l4.next = l5;
        l5.next = l6;

        printList(l1); // 1 -> 2 -> 4
        printList(l4); // 1 -> 3 -> 4

        ListNode3 merged = mergeTwoLists(l1, l4);
        printList(merged); // 1 -> 1 -> 2 -> 3 -> 4 -> 4
    }

    private static void printList(ListNode3 head) {
        StringBuilder sb = new StringBuilder();
        for (ListNode3 node = head; node != null; node = node.next) {
            sb.append(node.val);
            if (node.next != null) {
                sb.append(" -> ");
            }
        }
        System.out.println(sb);
        // йдемо від голови до кінця (null), між значеннями ставимо стрілку
    }

    private static ListNode3 mergeTwoLists(ListNode3 l1, ListNode3 l2) {
        ListNode3 dummy = new ListNode3(0);
        ListNode3 curr = dummy;

        while(l1 != null && l2 != null) {
            if (l1.val  <= l2.val) {
                curr.next = l1;
                l1 = l1.next;
            } else {
                curr.next = l2;
                l2 = l2.next;
            }
            curr = curr.next;
        }
        curr.next = l1 == null? l2 : l1;
        return dummy.next;
    }
}

class ListNode3 {
    int val;
    ListNode3 next;
    ListNode3(int x) {
        val = x;
    }
}
