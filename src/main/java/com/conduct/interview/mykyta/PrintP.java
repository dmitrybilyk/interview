package com.conduct.interview.mykyta;

public class PrintP {
    public static void main(String[] args) {
        int height = 9;   // висота "P" (можна міняти)
        int width  = 7;   // ширина "P" (можна міняти)

        int mid = height / 2; // рядок середини (де поперечна перекладина P)

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                boolean isP = (c == 0)                               // вертикальна ніжка зліва
                           || (r == 0)                              // верхня перекладина
                           || (r == mid)                            // середня перекладина
                           || (c == width - 1 && r > 0 && r < mid);  // права стінка "петельки" зверху

                System.out.print(isP ? 'P' : ' ');
            }
            System.out.println();
        }
    }
}
