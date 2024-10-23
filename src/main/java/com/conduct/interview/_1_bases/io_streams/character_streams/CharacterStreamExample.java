package com.conduct.interview._1_bases.io_streams.character_streams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CharacterStreamExample {
    private static final String FILE_PATH = "/home/dmytro/dev/projects/interview/src/main/java/" +
            "com/conduct/interview/_1_bases/io_streams/" +
            "output.txt";

    public static void main(String[] args) {
        // Writing to a file using BufferedWriter
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(
                        FILE_PATH))) {
            writer.write("Hello, World!");
            writer.newLine();
            writer.write("This is an example of character streams.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reading from a file using BufferedReader
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
