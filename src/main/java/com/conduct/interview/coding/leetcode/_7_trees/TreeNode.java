package com.conduct.interview.coding.leetcode._7_trees;

// Вузол бінарного дерева — типовий "будівельний блок" задач LeetCode про дерева.
// Поля public, бо клас використовується з підпакетів (_1_traversal, _2_dfs_problems).
public class TreeNode {
    public int val;
    public TreeNode left, right;
    public TreeNode(int x) {
        val = x;
    }
}
