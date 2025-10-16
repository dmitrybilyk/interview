package com.conduct.interview.coding.linked_lists.dll.lru_cache;

import java.util.HashMap;
import java.util.Map;

public class LruCacheCheck4 {
    public static void main(String[] args) {
        LruCache4 lruCache4 = new LruCache4(3);
        lruCache4.put(2, 2);
        lruCache4.put(3, 3);
        lruCache4.put(4, 4);
        lruCache4.put(5, 5);

        lruCache4.printAll();

        System.out.println(lruCache4.get(3));

        lruCache4.printAll();
    }
}

class LruCache4 {
    private Map<Integer, LruNode4> cache;
    int capacity;
    LruNode4 head;
    LruNode4 tail;

    public LruCache4(int capacity) {
        this.cache = new HashMap<>();
        this.capacity = capacity;
        head = new LruNode4(0, 0);
        tail = new LruNode4(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public void put(int key, int value) {
        LruNode4 node = cache.get(key);
        if (node != null) {
            node.value = value;
            cache.put(key, node);
            moveToFront(node);
        } else {
            LruNode4 newNode = new LruNode4(key, value);
            addToFront(newNode);
            cache.put(key, newNode);
            if (cache.size() > capacity) {
                removeNode(tail.prev);
                cache.remove(key);
            }
        }
    }

    public void printAll() {
        LruNode4 current = head.next;
        while (current.next != null) {
            System.out.println(current.value);
            current = current.next;
        }
    }

    private void moveToFront(LruNode4 node) {
        removeNode(node);
        addToFront(node);
    }

    private void addToFront(LruNode4 node) {
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
        node.prev = head;
    }

    private void removeNode(LruNode4 node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public int get(int key) {
        LruNode4 node = cache.get(key);
        if (node != null) {
            moveToFront(node);
            return node.value;
        }
        return -1;
    }
}

class LruNode4 {
    int key;
    int value;
    LruNode4 next;
    LruNode4 prev;

    public LruNode4(int key, int value) {
        this.key = key;
        this.value = value;
    }
}
