package com.conduct.interview.mykyta;

public class Pattern {
    public static void main(String[] args) {

        char P = 'P';

        int rows = 12;
        int cols = 12;

        char[][] a = new char[rows][cols];

        // заповнюємо пробілами
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                a[r][c] = ' ';
            }
        }

        // ліва лінія (хвиляста, як на малюнку)
        a[2][1] = P;
        a[3][1] = P;
        a[4][1] = P;
        a[5][1] = P;
        a[6][1] = P;
        a[7][1] = P;

        // верхній правий трикутник
        a[2][7] = P;
        a[2][8] = P;
        a[2][9] = P;

        a[3][8] = P;
        a[3][9] = P;

        a[4][9] = P;

        // нижній правий 3×3 блок
        a[8][7] = P; a[8][8] = P; a[8][9] = P;
        a[9][7] = P; a[9][8] = P; a[9][9] = P;
        a[10][7] = P; a[10][8] = P; a[10][9] = P;

        // виведення
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                System.out.print(a[r][c]);
            }
            System.out.println();
        }
    }
}
