package com.conduct.interview.coding.linked_lists;

public class NthElementInLinkedListCheck {
    public static void main(String[] args) {
        SimpleLinkedList simpleLinkedList = new SimpleLinkedList();
        simpleLinkedList.append(2);
        simpleLinkedList.append(3);
        simpleLinkedList.append(5);
        simpleLinkedList.append(3);
        simpleLinkedList.append(2);

//        simpleLinkedList.printAll();

        System.out.println(simpleLinkedList.getNthElement(3));
    }
}

class NodeEntry {
    int data;
    NodeEntry next;

    public NodeEntry(int data) {
        this.data = data;
    }
}

class SimpleLinkedList {
    NodeEntry head;

    void append(int data) {
        NodeEntry newNodeEntry = new NodeEntry(data);
        if (head == null) {
            head = newNodeEntry;
            return;
        }

        NodeEntry current = head;
        while(current.next != null) {
            current = current.next;
        }
        current.next = newNodeEntry;
    }

    void printAll() {
        NodeEntry current = head;

        System.out.println(current.data);
        while(current.next != null) {
            current = current.next;
            System.out.println(current.data);
        }
    }

    public int getNthElement(int n) {

        NodeEntry current = head;
        int count = 1;
        while(current != null) {
            if (count == n) {
                return current.data;
            }
            current = current.next;
            count++;
        }
        return -1;
    }
}