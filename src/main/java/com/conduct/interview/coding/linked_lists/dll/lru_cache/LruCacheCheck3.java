package com.conduct.interview.coding.linked_lists.dll.lru_cache;

import java.util.HashMap;
import java.util.Map;

public class LruCacheCheck3 {
    public static void main(String[] args) {
        LruCache3Check lruCache3Check = new LruCache3Check(3);
        lruCache3Check.put(2, 2);
        lruCache3Check.put(3, 3);
        lruCache3Check.put(4, 4);
        lruCache3Check.put(5, 5);
        lruCache3Check.put(6, 6);

        lruCache3Check.printAll();

        System.out.println(lruCache3Check.get(4));

        lruCache3Check.printAll();
    }
}

class LruCache3Check {
    private Map<Integer, LruNode> cache;
    private int capacity;
    private LruNode head;
    private LruNode tail;

    public LruCache3Check(int capacity) {
        this.cache = new HashMap<>();;
        this.capacity = capacity;
        head = new LruNode(0, 0);
        tail = new LruNode(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public void put(int key, int value) {
        LruNode newNode = new LruNode(key, value);
        cache.put(key, newNode);
        addToFront(newNode);
        if (cache.size() > capacity) {
            cache.remove(tail.prev.key);
            remove(tail.prev);
        }
    }

    public int get(int key) {
        LruNode node = cache.get(key);
        if (node == null) {
            return -1;
        } else {
            moveToFront(node);
            return node.value;
        }
    }

    private void addToFront(LruNode newNode) {
        newNode.next = head;
        head.prev = newNode;
        head = newNode;
    }

    public void printAll() {
        LruNode current = head;
        while(current != null) {
            System.out.println(current.value);
            current = current.next;
        }
    }

    private void moveToFront(LruNode node) {
        remove(node);
        addToFront(node);
    }

    private void remove(LruNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

}

class LruNode {
    int key;
    int value;
    LruNode next;
    LruNode prev;

    public LruNode(int key, int value) {
        this.key = key;
        this.value = value;
    }
}