package com.conduct.interview.coding.linked_lists.sll;

public class PallindromSllCheck {
    public static void main(String[] args) {
        SllNodeCheck head = new SllNodeCheck(3);
        head.next = new SllNodeCheck(5);
        head.next.next = new SllNodeCheck(2);
        head.next.next.next = new SllNodeCheck(3);

        printSllCheck(head);

        System.out.println(isPallindrom(head));
    }

    private static boolean isPallindrom(SllNodeCheck head) {
        SllNodeCheck slow = head;
        SllNodeCheck fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        SllNodeCheck secondHalf = slow;

        SllNodeCheck reversedSecondHalf = reverseSecondHalf(secondHalf);
        printSllCheck(secondHalf);

        return false;
    }

    private static SllNodeCheck reverseSecondHalf(SllNodeCheck secondHalf) {
        return null;
    }

    private static void printSllCheck(SllNodeCheck head) {
        SllNodeCheck current = head;
        while (current != null) {
            System.out.println(current.value);
            current = current.next;
        }
    }
}

class SllNodeCheck {
    int value;
    SllNodeCheck next;

    public SllNodeCheck(int value) {
        this.value = value;
    }
}
