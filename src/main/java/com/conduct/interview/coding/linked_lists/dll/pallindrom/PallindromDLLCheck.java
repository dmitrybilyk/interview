package com.conduct.interview.coding.linked_lists.dll.pallindrom;

public class PallindromDLLCheck {
    public static void main(String[] args) {
        DllNode dllNode = new DllNode(1);
        dllNode.next = new DllNode(2);
        dllNode.next.prev = dllNode;
        dllNode.next.next = new DllNode(3);
        dllNode.next.next.prev = dllNode.next;
        dllNode.next.next.next = new DllNode(3);
        dllNode.next.next.next.prev = dllNode.next.next;
        dllNode.next.next.next.next = new DllNode(2);
        dllNode.next.next.next.next.prev = dllNode.next.next.next;
        dllNode.next.next.next.next.next = new DllNode(1);
        dllNode.next.next.next.next.next.prev = dllNode.next.next.next.next;

        printDll(dllNode);

        System.out.println(isDllPallindrom(dllNode));
    }

    private static void printDll(DllNode dllNode) {
        DllNode current = dllNode;
        while (current != null) {
            System.out.println(current.value);
            current = current.next;
        }
    }

    private static boolean isDllPallindrom(DllNode dllNode) {
        DllNode head = dllNode;
        DllNode tail = null;

        DllNode current = dllNode;
        int length = 0;
        while (current != null) {
            tail = current;
            current = current.next;
            length++;
        }

        for (int i = 0; i < length / 2; i++) {
            if (head.value != tail.value) return false;
            head = head.next;
            tail = tail.prev;
        }
        return true;
    }
}

class DllNode {
    int value;
    DllNode next, prev;

    public DllNode(int value) {
        this.value = value;
    }
}