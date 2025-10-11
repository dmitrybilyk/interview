package com.conduct.interview.coding.linked_lists.sll.pallindrom;

public class PallindromSllCheck3 {
    static class SllNode3 {
        int value;
        SllNode3 next;

        public SllNode3(int value) {
            this.value = value;
        }
    }

    private static void printList(SllNode3 sllNode3) {
        while (sllNode3 != null) {
            System.out.println(sllNode3.value);
            sllNode3 = sllNode3.next;
        }
    }

    public static void main(String[] args) {
        SllNode3 sllNode3 = new SllNode3(9);
        sllNode3.next = new SllNode3(8);
        sllNode3.next.next = new SllNode3(7);
        sllNode3.next.next.next = new SllNode3(7);
        sllNode3.next.next.next.next = new SllNode3(8);
        sllNode3.next.next.next.next.next = new SllNode3(9);

        printList(sllNode3);

        SllNode3 slow = sllNode3;
        SllNode3 fast = sllNode3;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        SllNode3 reversedSecondHalf = reverseSecondHalf(slow);

        printList(reversedSecondHalf);

        System.out.println(isPallindrome(sllNode3, reversedSecondHalf));
    }

    private static boolean isPallindrome(SllNode3 sllNode3, SllNode3 reversedSecondHalf) {
        SllNode3 p1 = sllNode3;
        SllNode3 p2 = reversedSecondHalf;
        while(p2 != null) {
            if (p1.value != p2.value) {
                return false;
            }
            p1 = p1.next;
            p2 = p2.next;
        }
        return true;
    }

    private static SllNode3 reverseSecondHalf(SllNode3 secondHalf) {
        SllNode3 prev = null;
        SllNode3 curr = secondHalf;
        while (curr != null) {
            SllNode3 next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }
}
