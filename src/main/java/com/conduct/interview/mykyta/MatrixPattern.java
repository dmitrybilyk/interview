package com.conduct.interview.mykyta;

public class MatrixPattern {
    public static void main(String[] args) {

        int rows = 7;
        int cols = 10;
        char P = 'P';
        char[][] a = new char[rows][cols];

        // Заповнюємо пробілами
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                a[i][j] = ' ';

        // 1) Третій стовпець повністю P
        for (int r = 0; r < rows; r++) {
            a[r][2] = P;
        }

        // 2) Трикутник, РОЗВЕРНУТИЙ ПО ГОРИЗОНТАЛІ, сторона = 4
        int side = 4;

        for (int r = 0; r < side; r++) {           // рядки 0..3
            for (int c = 0; c < side - r; c++) {   // 4,3,2,1 елементів
                a[r][cols - side + r + c] = P;     // зміщення вправо
            }
        }

        // Вивід матриці
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                System.out.print(a[r][c]);
            }
            System.out.println();
        }
    }
}
