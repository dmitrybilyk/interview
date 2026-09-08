package com.conduct.interview.coding.leetcode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

// LC 20 — Valid Parentheses
// Pattern: Stack — push open brackets, pop and check on close
// Time O(n), Space O(n)
public class ValidParentheses {

    public static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        Map<Character, Character> pairs = Map.of(')', '(', ']', '[', '}', '{');
        for (char c : s.toCharArray()) {
            if (pairs.containsValue(c)) {
                stack.push(c);
            } else {
                if (stack.isEmpty() || stack.pop() != pairs.get(c)) return false;
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        System.out.println(isValid("()[]{}"));  // true
        System.out.println(isValid("(]"));      // false
        System.out.println(isValid("{[]}"));    // true
    }
}
