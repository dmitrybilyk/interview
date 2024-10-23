package com.conduct.interview._1_bases.io_streams;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This calss issdfsdf
 */
public class Main {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        // Открыть файл и использовать try-with-resources для автоматического закрытия
        try (FileInputStream fin = new FileInputStream("/home/dmytro/dev/projects/interview/src/main/java/com/conduct/interview/_1_bases/io_streams/test.txt")) {
            int i;

            // Читать байты, пока не встретится конец файла
            do {
                i = fin.read();
                if (i != -1) {
                    System.out.print((char) i); // Читать и выводить байты как символы
                }
            } while (i != -1);

        } catch (FileNotFoundException exc) {
            System.out.println("Файл не найден.");
        } catch (IOException exc) {
            System.out.println("Ошибка при чтении файла.");
        }
    }
}
