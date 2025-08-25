package com.conduct.interview._20_algorythms_and_data_structures.twodimarrays;

import java.util.Arrays;

public class RotateMatrix {
    public static void rotate(int[][] matrix) {
        int n = matrix.length;

        // Step 1: Transpose the matrix
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // Step 2: Reverse each row
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n / 2; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[i][n - 1 - j];
                matrix[i][n - 1 - j] = temp;
            }
        }
    }

    public static void main(String[] args) {
        int[][] matrix = {
                {1, 2, 3, 6},
                {4, 5, 6, 7},
                {7, 8, 9, 2},
                {3, 7, 2, 1}
        };

        rotate(matrix);

        // Print rotated matrix
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}
