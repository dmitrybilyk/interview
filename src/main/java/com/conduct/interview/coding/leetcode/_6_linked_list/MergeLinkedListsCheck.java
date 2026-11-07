package com.conduct.interview.coding.leetcode._6_linked_list;

// LC 21 — Merge Two Sorted Lists
// Задача: об'єднати два ВІДСОРТОВАНІ зв'язні списки в один відсортований список.
// Ідея: dummy-вузол як "заглушка" для старту, далі завжди приєднуємо
// вузол з меншим значенням і рухаємо відповідний вказівник вперед.
// Час O(n + m), Пам'ять O(1) (переставляємо існуючі вузли, нових не створюємо)
public class MergeLinkedListsCheck {
    public static void main(String[] args) {
        ListNode2 l1 = new ListNode2(1);
        ListNode2 l2 = new ListNode2(2);
        ListNode2 l3 = new ListNode2(4);
        // перший список: 1 -> 2 -> 4

        ListNode2 l4 = new ListNode2(1);
        ListNode2 l5 = new ListNode2(3);
        ListNode2 l6 = new ListNode2(4);
        // другий список: 1 -> 3 -> 4

        l1.next = l2;
        l2.next = l3;

        l4.next = l5;
        l5.next = l6;

        printList(l1); // 1 -> 2 -> 4
        printList(l4); // 1 -> 3 -> 4

        ListNode2 merged = mergeTwoLists(l1, l4);
        printList(merged); // 1 -> 1 -> 2 -> 3 -> 4 -> 4
    }

    private static void printList(ListNode2 head) {
        StringBuilder sb = new StringBuilder();
        for (ListNode2 node = head; node != null; node = node.next) {
            sb.append(node.val);
            if (node.next != null) {
                sb.append(" -> ");
            }
        }
        System.out.println(sb);
        // йдемо від голови до кінця (null), між значеннями ставимо стрілку
    }

    private static ListNode2 mergeTwoLists(ListNode2 l1, ListNode2 l2) {
        ListNode2 dummy = new ListNode2(0);
        
        ListNode2 curr = dummy;
        
        while (l1 != null  && l2 != null) {
            if (l1.val <= l2.val) {
                curr.next = l1;
                l1 = l1.next;
            } else {
                curr.next = l2;
                l2 = l2.next;
            }
            curr = curr.next;
        }
        curr.next = l2 == null? l1: l2;
        return dummy.next;
    }
}

class ListNode2 {
    int val;
    ListNode2 next;
    ListNode2(int x) {
        val = x;
    }
}
