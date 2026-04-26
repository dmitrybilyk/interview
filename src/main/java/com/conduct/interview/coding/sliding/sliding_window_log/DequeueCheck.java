package com.conduct.interview.coding.sliding.sliding_window_log;

import java.util.ArrayDeque;
import java.util.Deque;

public class DequeueCheck {
    public static void main(String[] args) {
        Deque<Long> deque = new ArrayDeque<>();
        deque.add(5L);
        deque.add(6L);
        deque.add(7L);
        System.out.println(deque.peekFirst());
        System.out.println(deque.pollFirst());
        System.out.println(deque.pollLast());
//        System.out.println(deque.poll());
//        System.out.println(deque.peek());
//        System.out.println(deque.pop());
        System.out.println(deque);
    }
}
