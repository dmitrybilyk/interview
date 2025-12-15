package com.conduct.interview.mykyta.alg;

public class AlgorithmSimulation {

    public static void main(String[] args) {
        // Початкові значення
        int a = 4;
        int b = 2;
        int c; // 'c' ініціалізується пізніше
        
        // Рядок для збору виводу
        StringBuilder output = new StringBuilder();

        // Лічильник для запобігання нескінченному циклу (введено для безпеки)
        int safetyCounter = 0;
        final int MAX_INNER_ITERATIONS = 5;

        System.out.println("Старт симуляції. Початкові значення: a=" + a + ", b=" + b);

        // 1. Зовнішній цикл: a < 7
        while (a < 7) {
            output.append("\n[Зовнішній цикл: a=").append(a).append("]");

            // 2. Середній цикл: b < 3
            if (b < 3) {
                
                // 3. Ініціалізація c (перед середнім циклом)
                // Оскільки 'c' не ініціалізовано на початку програми, ми вважаємо, що 
                // умова 'c < a' спочатку виконується. На першому вході в блок:
                c = b; // c = 2
                output.append("\n   [Умова b<3]: c ініціалізовано: c=").append(c);
                
                // 4. Внутрішній цикл: c < a
                while (c < a) {
                    safetyCounter++;
                    if (safetyCounter > MAX_INNER_ITERATIONS + 2) {
                        output.append("\n   *** ПРОГРАМА ВИЯВИЛА НЕСКІНЧЕННИЙ ЦИКЛ (c < a) і зупинена! ***");
                        System.out.println(output.toString());
                        System.out.println("\nФактичний вивід: " + output.toString().replaceAll("[^AB]", ""));
                        return; // Зупинка програми
                    }
                    
                    output.append("\n   [Внутрішній цикл: c=").append(c).append(", a=").append(a).append("]");

                    // Вкладене розгалуження: c == 3
                    if (c == 3) {
                        // Гілка (T)
                        b = b + 1; 
                        output.append(" -> b стало: ").append(b);
                        output.append("B");
                    } else {
                        // Гілка (Н)
                        a = a + 1; 
                        c = c + 1; 
                        output.append(" -> a стало: ").append(a).append(", c стало: ").append(c);
                        output.append("A");
                    }
                } // Кінець внутрішнього циклу c < a
                
                output.append("\n   [Вихід з c < a]");

            } // Кінець середнього блоку (b < 3)
            
            // 5. Дія після середнього циклу: piš "B"
            output.append("B"); 

            // 6. Умова після piš "B": a < 6
            if (a < 6) {
                // Гілка (+)
                b = b - 1;
            } else {
                // Гілка (-)
                // Дія відсутня, переходимо до piš "C"
            }
            
            // 7. Фінальна дія зовнішнього циклу
            output.append("C"); 

        } // Кінець зовнішнього циклу a < 7

        System.out.println(output.toString());
        System.out.println("\n--- Фактичний вивід (з обмеженням циклу) ---");
        System.out.println(output.toString().replaceAll("[^ABC]", ""));
    }
}