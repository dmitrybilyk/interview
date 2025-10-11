package com.conduct.interview.coding.linked_lists.sll.binary_to_decimal;

class ListNode7 {
    int val;
    ListNode7 next;
    ListNode7(int val) { this.val = val; }
}

public class BinaryToDecimal {
    public static int getDecimalValue(ListNode7 head) {
        int result = 0;
        ListNode7 current = head;

        while (current != null) {
            // multiply by 2 (shift left) and add current bit
            result = result * 2 + current.val;
            current = current.next;
        }
        return result;
    }

    public static void main(String[] args) {
        // Example: binary 101
        ListNode7 head = new ListNode7(1);
        head.next = new ListNode7(0);
        head.next.next = new ListNode7(1);

        System.out.println(getDecimalValue(head)); // Output: 5
    }
}
