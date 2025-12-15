package com.conduct.interview.mykyta.recursionAlik;

//1 2 4 6 0 2 4

//        12345670
//        12345670
//        1234567


public class AlikProgram {

    // Глобальний вхідний рядок для імітації потоку введення
    private static final String INPUT_STREAM = "12345670123456701234567";
    // Індекс для відстеження, який символ буде прочитано наступним
    private static int readIndex = 0;

    // Метод, який імітує функцію 'накти()'
    private static int nacti() {
        if (readIndex < INPUT_STREAM.length()) {
            // Отримуємо символ, перетворюємо його на число і збільшуємо індекс
            char charToRead = INPUT_STREAM.charAt(readIndex);
            readIndex++;
            // Виводимо в консоль, що саме було прочитано, для зручності відстеження
            System.err.println("Читання [" + (readIndex - 1) + "]: " + charToRead); 
            return Character.getNumericValue(charToRead);
        } else {
            // Повертаємо -1, якщо вхід закінчився (але за умовами задачі вхід не закінчиться до кінця виводу)
            return -1; 
        }
    }

    // Рекурсивна функція 'alik()'
    public static void alik() {
        // --- 1. Початок виконання ---
        int i = nacti();

        // Якщо вхід закінчився, виходимо
        if (i == -1) {
            return;
        }

        // --- 2. Основна логіка (Розгалуження) ---

        if (i == 0) {
            // Шлях (+) для i == 0
            i = nacti();
        } else if (i > 5) {
            // Шлях (-, +) для i > 5
            i = nacti();
            
            // Алік (Рекурсія)
            alik();
            
            // Виконується після повернення з рекурсії
            i = nacti();
        } else {
            // Шлях (-, -) для i від 1 до 5
            
            // Алік (Рекурсія)
            alik();
            
            // Виконується після повернення з рекурсії
            i = nacti();
            i = nacti();
        }
        
        // --- 3. Фінальний вивід ---
        // piš i
        System.out.print(i);
    }

    public static void main(String[] args) {
        System.out.println("--- Старт програми 'Алік' ---");
        alik();
        System.out.println("\n--- Кінець програми ---");
    }
}