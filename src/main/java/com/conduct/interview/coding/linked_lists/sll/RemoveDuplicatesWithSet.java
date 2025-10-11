package com.conduct.interview.coding.linked_lists.sll;

import com.conduct.interview.coding.linked_lists.sll.binary_to_decimal.ListNode7;

import java.util.HashSet;

class ListNode {
    int val;
    ListNode7 next;
    ListNode(int val) {
        this.val = val;
    }
}

class Solution {
    public ListNode7 deleteDuplicates(ListNode7 head) {
        // Handle empty list or single node
        if (head == null || head.next == null) {
            return head;
        }

        // Use HashSet to track seen values
        HashSet<Integer> seen = new HashSet<>();
        ListNode7 current = head;
        seen.add(current.val); // Add first node's value

        // Traverse the list
        while (current != null && current.next != null) {
            if (seen.contains(current.next.val)) {
                // If next value is a duplicate, skip it
                current.next = current.next.next;
            } else {
                // Add new value and move forward
                seen.add(current.next.val);
                current = current.next;
            }
        }

        return head;
    }
}

public class RemoveDuplicatesWithSet {
    public static void main(String[] args) {
        // Create test linked list: 1->1->2->3->3
        ListNode7 head = new ListNode7(1);
        head.next = new ListNode7(1);
        head.next.next = new ListNode7(2);
        head.next.next.next = new ListNode7(3);
        head.next.next.next.next = new ListNode7(3);

        Solution solution = new Solution();
        ListNode7 result = solution.deleteDuplicates(head);
        // Print the result
        while (result != null) {
            System.out.print(result.val + "->");
            result = result.next;
        }
        System.out.println("null");
    }
}