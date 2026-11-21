package com.conduct.interview.coding.leetcode._7_trees._1_traversal;

import com.conduct.interview.coding.leetcode._7_trees.TreeNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// LC 144 — Preorder, LC 94 — Inorder, LC 145 — Postorder Traversal
// Pattern: DFS — йдемо вглиб однієї гілки до кінця, потім повертаємось
// Time O(n), Space O(h) — h це висота дерева (глибина рекурсії / розмір стеку)

// Задача: обійти всі вузли дерева "вглиб". Три варіанти відрізняються лише тим,
// КОЛИ ми записуємо значення поточного вузла відносно його дітей:
//   Preorder  (прямий):     вузол → ліве → праве     (Node first)
//   Inorder   (симетричний): ліве → вузол → праве     (Node in the middle)
//   Postorder (зворотний):  ліве → праве → вузол     (Node last)
// Підказка для запам'ятовування: "pre/in/post" — це позиція ВУЗЛА, ліве завжди перед правим.
public class DfsTraversal {

    public static List<Integer> preorder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preorder(root, result);
        return result;
    }

    private static void preorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        result.add(node.val);
        // спочатку сам вузол — так копіюють/серіалізують дерево (корінь іде першим)
        preorder(node.left, result);
        preorder(node.right, result);
    }

    public static List<Integer> inorder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    private static void inorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        inorder(node.left, result);
        result.add(node.val);
        // вузол між лівим і правим — для BST це дає ВІДСОРТОВАНИЙ порядок
        inorder(node.right, result);
    }

    public static List<Integer> postorder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postorder(root, result);
        return result;
    }

    private static void postorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        postorder(node.left, result);
        postorder(node.right, result);
        result.add(node.val);
        // вузол останнім — спершу треба знати відповідь від дітей (висота, видалення дерева)
    }

    // Inorder без рекурсії — часте питання на співбесіді: "а тепер без рекурсії"
    // Ідея: явний стек замінює стек викликів. Спускаємось вліво до упору, складаючи вузли в стек,
    // потім дістаємо вузол, записуємо його і переходимо в праве піддерево.
    public static List<Integer> inorderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
                // йдемо вліво до упору — ліве піддерево обробляється першим
            }

            current = stack.pop();
            result.add(current.val);
            // ліве піддерево вже оброблене -> записуємо сам вузол

            current = current.right;
            // і повторюємо все те саме для правого піддерева
        }

        return result;
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

        System.out.println("Preorder:  " + preorder(root));         // [1, 2, 4, 5, 3, 6]
        System.out.println("Inorder:   " + inorder(root));          // [4, 2, 5, 1, 3, 6]
        System.out.println("Postorder: " + postorder(root));        // [4, 5, 2, 6, 3, 1]
        System.out.println("Inorder (iterative): " + inorderIterative(root)); // [4, 2, 5, 1, 3, 6]
    }
}
