package com.conduct.interview.mykyta;

public class LetterP {
    public static void main(String[] args) {
        // Розміри такі ж, як у завданні: 7 рядків × 10 стовпців
        for (int a = 1; a <= 7; a++) {          // зовнішній цикл — рядки
            for (int b = 1; b <= 10; b++) {     // внутрішній цикл — стовпці
                
                // Умова: вертикальна лінія ІЛИ трикутник справа
                if (b == 3 || b - a >= 6) {
                    System.out.print("P");
                } else {
                    System.out.print("o");
                }
            }
            System.out.println(); // перехід на новий рядок після кожного рядка
        }
    }
}