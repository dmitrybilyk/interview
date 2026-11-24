package com.conduct.interview.coding.leetcode._7_trees._1_traversal.dfs;

import com.conduct.interview.coding.leetcode._7_trees.TreeNode;

import java.util.ArrayList;
import java.util.List;

// LC 144 — Binary Tree Preorder Traversal
// Pattern: DFS (Depth-First Search — пошук у глибину), порядок: вузол → ліве → праве
// Time O(n), Space O(h) — h це висота дерева (глибина рекурсії)

// Задача: обійти дерево, записуючи вузол ПЕРЕД його дітьми ("pre" = вузол перший).
// Коли використовувати: копіювання/серіалізація дерева (корінь іде першим),
// шлях від кореня вниз.
public class PreorderTraversal {

    // result передаємо в рекурсію, щоб усі виклики додавали в ОДИН список (без копіювання)
    public static void preorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        result.add(node.val);
        // спочатку сам вузол — так копіюють/серіалізують дерево (корінь іде першим)
        preorder(node.left, result);
        preorder(node.right, result);
    }

    public static void main(String[] args) {
        /*
                1
               / \
              2   3
             / \   \
            4   5   6
        */
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.right = new TreeNode(6);

        List<Integer> result = new ArrayList<>();
        preorder(root, result);
        System.out.println(result); // [1, 2, 4, 5, 3, 6]
    }
}
