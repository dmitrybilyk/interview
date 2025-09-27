package com.conduct.interview.coding.linked_lists.partition_list;

public class PartitionListCheck {
    static class ListNode {
        int value;
        ListNode next;

        public ListNode(int value) {
            this.value = value;
        }
    }

    ListNode head;

    public static void main(String[] args) {
        ListNode listNode = new ListNode(3);
        listNode.next = new ListNode(5);
        listNode.next.next = new ListNode(1);
        listNode.next.next.next = new ListNode(6);
        listNode.next.next.next.next = new ListNode(5);
        listNode.next.next.next.next.next = new ListNode(5);
        listNode.next.next.next.next.next.next = new ListNode(2);
        listNode.next.next.next.next.next.next.next = new ListNode(7);

        PartitionListCheck partitionListCheck = new PartitionListCheck();
        partitionListCheck.printLinkedList(listNode);

        partitionListCheck.printLinkedList(partitionListNode(listNode, 4));
    }

    private static ListNode partitionListNode(ListNode listNode, int partitionValue) {
        ListNode current = listNode;

        ListNode dummy1 = new ListNode(0);
        ListNode dummy2 = new ListNode(0);

        ListNode prev1 = dummy1;
        ListNode prev2 = dummy2;

        while (current != null) {
            if (current.value < partitionValue) {
                prev1.next = current;
                prev1 = current;
            } else {
                prev2.next = current;
                prev2 = current;
            }
            current = current.next;
        }

        dummy1.next = prev1;
        dummy2.next = null;
        prev1.next = dummy2;

        return dummy1;

    }

    private void printLinkedList(ListNode node) {
        ListNode current = node;
        while (current != null) {
            System.out.println(current.value);
            current = current.next;
        }
    }
}

