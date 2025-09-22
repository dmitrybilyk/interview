package com.conduct.interview.coding.arrays.twodimarrays;

public class RotateMatrixCheck2 {
    public static void main(String[] args) {
        int[][] array = {
            {3, 4, 5},
            {3, 5, 7},
            {2, 8, 3}
        };

        print2DimArray(array);
        rotate90(array);
        print2DimArray(array);
    }

    public static int[][] rotate90(int[][] array) {
        int n = array.length;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = array[i][j];
                array[i][j] = array[j][i];
                array[j][i] = temp;
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n / 2; j++) {

            }
        }
        return array;
    }

    private static void print2DimArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }
}
