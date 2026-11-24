package com.conduct.interview.coding.leetcode._7_trees._1_traversal.dfs;

import com.conduct.interview.coding.leetcode._7_trees.TreeNode;

import java.util.ArrayList;
import java.util.List;

// LC 145 — Binary Tree Postorder Traversal
// Pattern: DFS (Depth-First Search — пошук у глибину), порядок: ліве → праве → вузол
// Time O(n), Space O(h) — h це висота дерева (глибина рекурсії)

// Задача: обійти дерево, записуючи вузол ПІСЛЯ його дітей ("post" = вузол останній).
// Коли використовувати: коли для відповіді у вузлі спершу потрібні відповіді дітей —
// висота, діаметр, видалення дерева (спочатку діти, потім батько).
public class PostorderTraversal {

    // result передаємо в рекурсію, щоб усі виклики додавали в ОДИН список (без копіювання)
    public static void postorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        postorder(node.left, result);
        postorder(node.right, result);
        result.add(node.val);
        // вузол останнім — спершу треба знати відповідь від дітей (висота, видалення дерева)
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
        postorder(root, result);
        System.out.println(result); // [4, 5, 2, 6, 3, 1]
    }
}
