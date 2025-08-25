package com.conduct.interview._20_algorythms_and_data_structures.twodimarrays;

import java.util.ArrayList;
import java.util.List;

public class SpiralMatrixCheck {
    public static void main(String[] args) {
//        2, 3, 5
//        5, 7, 8
//        1, 3, 9

//        2, 3, 5, 8, 9, 3, 1, 5, 7

        int[][] matrix = {{2, 3, 5}, {5, 7, 8}, {1, 3, 9}};

//        for (int i = 0; i < matrix.length; i++) {
//            for (int j = 0; j < matrix[0].length; j++) {
//                System.out.println(matrix[i][j]);
//            }
//        }

        System.out.println(spiralMatrix(matrix));

    }

    private static List<Integer> spiralMatrix(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        int top = 0;
        int bottom = matrix.length - 1;
        int right  = matrix[0].length - 1;
        int left = 0;

        while (top <= bottom && left <= right) {
            for (int i = left; i <= right; i++) {
                result.add(matrix[top][i]);
            }
            top++;

            for (int i = top; i <= bottom; i++) {
                result.add(matrix[i][right]);
            }
            right--;

            if (top <= bottom) {
                for (int i = right; i >= left; i--) {
                    result.add(matrix[bottom][i]);
                }
                bottom--;
            }

            if (left <= right) {
                for (int i = bottom; i >= top; i--) {
                    result.add(matrix[i][left]);
                }
                left++;
            }

        }

        return result;
    }
}
