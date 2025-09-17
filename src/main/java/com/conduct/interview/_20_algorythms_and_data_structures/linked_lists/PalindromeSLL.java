package com.conduct.interview._20_algorythms_and_data_structures.linked_lists;

class SllNode {
    int val;
    SllNode next;
    SllNode(int val) { this.val = val; }
}

public class PalindromeSLL {
    public static boolean isPalindrome(SllNode head) {
        if (head == null || head.next == null) return true;

        // 1. Find middle using slow/fast pointers
        SllNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // 2. Reverse second half
        SllNode secondHalf = reverse(slow);

        // 3. Compare first and second half
        SllNode p1 = head, p2 = secondHalf;
        while (p2 != null) {
            if (p1.val != p2.val) return false;
            p1 = p1.next;
            p2 = p2.next;
        }

        return true;
    }

    private static SllNode reverse(SllNode head) {
        SllNode prev = null, curr = head;
        while (curr != null) {
            SllNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }

    // Demo
    public static void main(String[] args) {
        SllNode head = new SllNode(1);
        head.next = new SllNode(2);
        head.next.next = new SllNode(3);
        head.next.next.next = new SllNode(2);
        head.next.next.next.next = new SllNode(1);

        System.out.println(isPalindrome(head)); // true
    }
}
