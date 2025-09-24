package com.conduct.interview.coding.arrays.twodimarrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpiralMatrixCheck2 {
    public static void main(String[] args) {
        int[][] matrix = {
                {2, 4, 1},
                {6, 2, 3},
                {4, 5, 3}
        };
        printMatrix(matrix);
        System.out.println(spiralMatrix(matrix));
    }

    private static List<Integer> spiralMatrix(int[][] matrix) {
        int left = 0;
        int top = 0;
        int right  = matrix[0].length - 1;
        int bottom = matrix.length - 1;
        List<Integer> list = new ArrayList<>();
        System.out.println(list);

        while(left <= right && top <= bottom) {
            for (int i = left; i <= right; i++) {
                list.add(matrix[top][i]);
            }
            top++;
            for (int i = top; i <= bottom; i++) {
                list.add(matrix[i][right]);
            }
            right--;
            if (top <= bottom) {
                for (int i = right; i >= left; i--) {
                    list.add(matrix[bottom][i]);
                }
                bottom--;
            }
            if (left <= right) {
                for (int i = bottom; i >= top; i--) {
                    list.add(matrix[i][left]);
                }
                left++;
            }
        }

        return list;
    }

    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
