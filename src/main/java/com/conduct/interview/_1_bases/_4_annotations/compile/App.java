package com.conduct.interview._1_bases._4_annotations.compile;


public class App {

    // Сценарій 1: Валідний метод. Все скомпілюється успішно.
    @Get
    public String getName() {
        return "Dmytro";
    }

    // Сценарій 2: Помилка! Назва методу порушуватиме правила нашого процесора.
    // Розкоментуй цей метод нижче, щоб побачити силу Compile-time процесора!

    @Get
    public String fetchDataFromDb() {
        return "Some payload";
    }


    public static void main(String[] args) {
        System.out.println("Додаток успішно скомпілювався та запустився!");
    }
}