package com.conduct.interview.coding.leetcode._7_trees._1_traversal.dfs;

import com.conduct.interview.coding.leetcode._7_trees.TreeNode;

import java.util.ArrayList;
import java.util.List;

// LC 94 — Binary Tree Inorder Traversal
// Pattern: DFS (Depth-First Search — пошук у глибину), порядок: ліве → вузол → праве
// Time O(n), Space O(h) — h це висота дерева (глибина рекурсії)

// Задача: обійти дерево, записуючи вузол МІЖ лівим і правим піддеревом ("in" = вузол посередині).
// Коли використовувати: для BST inorder дає ВІДСОРТОВАНИЙ порядок —
// k-тий найменший елемент, перевірка чи дерево є BST.
public class InorderTraversal {

    // result передаємо в рекурсію, щоб усі виклики додавали в ОДИН список (без копіювання)
    public static void inorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        inorder(node.left, result);
        result.add(node.val);
        // вузол між лівим і правим — для BST це дає ВІДСОРТОВАНИЙ порядок
        inorder(node.right, result);
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
        inorder(root, result);
        System.out.println(result); // [4, 2, 5, 1, 3, 6]
    }
}
