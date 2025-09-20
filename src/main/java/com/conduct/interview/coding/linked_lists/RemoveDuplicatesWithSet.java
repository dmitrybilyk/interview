package com.conduct.interview.coding.linked_lists;

import java.util.HashSet;

class ListNode {
    int val;
    ListNode next;
    ListNode(int val) {
        this.val = val;
    }
}

class Solution {
    public ListNode deleteDuplicates(ListNode head) {
        // Handle empty list or single node
        if (head == null || head.next == null) {
            return head;
        }

        // Use HashSet to track seen values
        HashSet<Integer> seen = new HashSet<>();
        ListNode current = head;
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
        ListNode head = new ListNode(1);
        head.next = new ListNode(1);
        head.next.next = new ListNode(2);
        head.next.next.next = new ListNode(3);
        head.next.next.next.next = new ListNode(3);

        Solution solution = new Solution();
        ListNode result = solution.deleteDuplicates(head);
        // Print the result
        while (result != null) {
            System.out.print(result.val + "->");
            result = result.next;
        }
        System.out.println("null");
    }
}