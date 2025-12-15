package com.conduct.interview.mykyta;

public class Ppppp {
    public static void main(String[] args) {

        for (int a = 1; a <= 7; a++) {           // рядки
            for (int b = 1; b <= 7; b++) {         // стовпці

                // умова — точно як на твоєму малюнку
                if (b == 1 ||                        // ліва вертикальна лінія (всі рядки)
                        a == 1 ||                        // верхній рядок повністю
                        a == 4 ||                        // середній рядок повністю (перемичка)
                        (a >= 2 && a <= 3 && b == 7)) {  // права сторона «вушка» тільки в рядках 2 і 3

                    System.out.print("× ");
                } else {
                    System.out.print("  ");          // два пробіли замість 0 — щоб виглядало акуратно
                }
            }
            System.out.println();    // новий рядок
        }
    }
    }
