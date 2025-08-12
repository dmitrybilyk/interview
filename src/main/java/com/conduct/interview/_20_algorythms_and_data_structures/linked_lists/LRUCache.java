package com.conduct.interview._20_algorythms_and_data_structures.linked_lists;

import java.util.HashMap;

public class LRUCache {
    private final int maxCacheSize;
    private final HashMap<Integer, LinkedListNode> map;
    private LinkedListNode listHead = null;
    private LinkedListNode listTail = null;

    public LRUCache(int maxSize) {
        this.maxCacheSize = maxSize;
        this.map = new HashMap<>();
    }

    /** Get value for key and mark as most recently used. */
    public String getValue(int key) {
        LinkedListNode item = map.get(key);
        if (item == null) return null;

        // Move to front to mark as most recently used
        if (item != listHead) {
            removeFromLinkedList(item);
            insertAtFrontOfLinkedList(item);
        }
        return item.value;
    }

    /** Remove node from linked list. */
    private void removeFromLinkedList(LinkedListNode node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        if (node == listTail) {
            listTail = node.prev;
        }
        if (node == listHead) {
            listHead = node.next;
        }

        node.prev = null;
        node.next = null;
    }

    /** Insert node at front of linked list. */
    private void insertAtFrontOfLinkedList(LinkedListNode node) {
        if (listHead == null) {
            listHead = node;
            listTail = node;
        } else {
            node.next = listHead;
            listHead.prev = node;
            listHead = node;
        }
    }

    /** Remove key/value pair from cache. */
    public boolean removeKey(int key) {
        LinkedListNode node = map.get(key);
        if (node == null) return false;
        removeFromLinkedList(node);
        map.remove(key);
        return true;
    }

    /** Put key, value pair in cache. */
    public void setKeyValue(int key, String value) {
        // Remove if already there
        removeKey(key);

        // If full, remove least recently used item
        if (map.size() >= maxCacheSize && listTail != null) {
            removeKey(listTail.key);
        }

        // Insert new node
        LinkedListNode node = new LinkedListNode(key, value);
        insertAtFrontOfLinkedList(node);
        map.put(key, node);
    }

    /** Node in doubly linked list */
    private static class LinkedListNode {
        LinkedListNode next;
        LinkedListNode prev;
        int key;
        String value;

        LinkedListNode(int k, String v) {
            this.key = k;
            this.value = v;
        }
    }

    // Demo
    public static void main(String[] args) {
        LRUCache LRUCache = new LRUCache(3);
        LRUCache.setKeyValue(1, "One");
        LRUCache.setKeyValue(2, "Two");
        LRUCache.setKeyValue(3, "Three");
        System.out.println(LRUCache.getValue(1)); // One
        LRUCache.setKeyValue(4, "Four"); // Removes least recently used (key=2)
        System.out.println(LRUCache.getValue(2)); // null
    }
}
