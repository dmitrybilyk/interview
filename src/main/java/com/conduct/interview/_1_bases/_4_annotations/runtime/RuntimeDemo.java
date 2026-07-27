package com.conduct.interview._1_bases._4_annotations.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

// 1. Наша власна перевикористовувана анотація
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) // Обов'язково RUNTIME, щоб її бачила рефлексія
@interface Benchmark {}

// 2. Окремий універсальний процесор, який можна перевикористовувати для будь-яких класів
class BenchmarkProcessor {
    public static void process(Object target) {
        Class<?> clazz = target.getClass();
        
        // Скануємо всі методи класу в пам'яті
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Benchmark.class)) {
                try {
                    long start = System.nanoTime();
                    
                    method.setAccessible(true); // Дозволяє викликати навіть private методи
                    method.invoke(target);      // Викликаємо метод
                    
                    long end = System.nanoTime();
                    double duration = (end - start) / 1_000_000.0;
                    System.out.println("[💥 RUNTIME PROCESSOR] Method '" + method.getName() + "' took " + duration + " ms");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

// 3. Класи різних розробників, які перевикористовують нашу анотацію
class UserService {
    @Benchmark
    public void loadUsersFromDatabase() throws InterruptedException {
        Thread.sleep(80); // Імітація роботи
    }
}

class EmailSender {
    @Benchmark
    private void sendWelcomeEmail() throws InterruptedException {
        Thread.sleep(30); // Імітація роботи
    }
}

// 4. Головний клас для запуску демо
public class RuntimeDemo {
    public static void main(String[] args) {
        UserService userService = new UserService();
        EmailSender emailSender = new EmailSender();

        // Процесор успішно обробляє абсолютно різні класи!
        BenchmarkProcessor.process(userService);
        BenchmarkProcessor.process(emailSender);
    }
}