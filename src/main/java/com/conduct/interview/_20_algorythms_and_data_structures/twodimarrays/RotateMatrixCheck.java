package com.conduct.interview._20_algorythms_and_data_structures.twodimarrays;

import java.util.Arrays;

public class RotateMatrixCheck {
    public static void main(String[] args) {
        int[][] matrix = {
                {4, 5, 2},
                {1, 3, 6},
                {8, 3, 1}
        };

        for (int[] row : rotate90(matrix)) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static int[][] rotate90(int[][] matrix) {
//        first need to swap around the diagonal
        int n = matrix.length;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

//        secondly need to revers every row
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n/2; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[i][n - 1 - j];
                matrix[i][n - 1 - j] = temp;
            }
        }

        return matrix;
    }
}
