package com.conduct.interview.coding.linked_lists.binary_to_decimal;

class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

public class BinaryToDecimal {
    public static int getDecimalValue(ListNode head) {
        int result = 0;
        ListNode current = head;

        while (current != null) {
            // multiply by 2 (shift left) and add current bit
            result = result * 2 + current.val;
            current = current.next;
        }
        return result;
    }

    public static void main(String[] args) {
        // Example: binary 101
        ListNode head = new ListNode(1);
        head.next = new ListNode(0);
        head.next.next = new ListNode(1);

        System.out.println(getDecimalValue(head)); // Output: 5
    }
}
