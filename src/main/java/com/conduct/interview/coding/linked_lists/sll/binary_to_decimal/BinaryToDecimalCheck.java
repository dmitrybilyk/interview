package com.conduct.interview.coding.linked_lists.sll.binary_to_decimal;

public class BinaryToDecimalCheck {
    public static void main(String[] args) {
        BinaryListNode binaryListNode = new BinaryListNode(0);
        binaryListNode.next = new BinaryListNode(0);
        binaryListNode.next.next = new BinaryListNode(1);

        System.out.println(fromBinaryToDecimal(binaryListNode));
    }

    private static int fromBinaryToDecimal(BinaryListNode binaryListNode) {
        BinaryListNode current = binaryListNode;
        int result = 0;
        while (current != null) {
            result = result * 2 + current.value;
            current = current.next;
        }
        return result;
    }
}

class BinaryListNode {
    int value;
    BinaryListNode next;

    public BinaryListNode(int value) {
        this.value = value;
    }
}
