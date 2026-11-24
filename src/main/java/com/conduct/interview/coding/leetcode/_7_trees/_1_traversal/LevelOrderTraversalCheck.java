package com.conduct.interview.coding.leetcode._7_trees._1_traversal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// LC 102 — Binary Tree Level Order Traversal
// Pattern: BFS (Breadth-First Search — пошук у ширину) with a Queue — snapshot level size before processing children
// Time O(n), Space O(n)

// Задача: обійти бінарне дерево рівень за рівнем і повернути список списків —
// кожен внутрішній список містить значення вузлів одного рівня.
// Ідея: використовуємо чергу (Queue). Перед обробкою кожного рівня фіксуємо
// її розмір — це кількість вузлів на поточному рівні. Обробляємо рівно стільки
// вузлів і додаємо їхніх дітей у хвіст черги (вони стануть наступним рівнем).
public class LevelOrderTraversalCheck {

    public static List<List<Integer>> levelOrder(TreeNode root) {
       List<List<Integer>> result = new ArrayList<>();

       if (root == null) {
           return result;
       }

       Deque<TreeNode> queue = new ArrayDeque<>();
       queue.offer(root);

       while (!queue.isEmpty()) {
           int levelSize = queue.size();

           List<Integer> level = new ArrayList<>();

           for(int i = 0; i < levelSize; i++) {
               TreeNode node = queue.poll();
               level.add(node.val);

               if (node.left != null) {
                   queue.offer(node.left);
               }
               if (node.right != null) {
                   queue.offer(node.right);
               }
           }
           result.add(level);
       }

       return result;
    }

    public static void main(String[] args) {
        TreeNode tree = new TreeNode(3);
        tree.left = new TreeNode(2);
        tree.right = new TreeNode(5);
        tree.left.left = new TreeNode(2);
        tree.left.right = new TreeNode(8);
        tree.right.left = new TreeNode(2);
        tree.right.right = new TreeNode(2);

        System.out.println(levelOrder(tree));
    }

    static class TreeNode {
        int val;
        TreeNode(int val) {
            this.val = val;
        }

        TreeNode left, right;
    }
}
