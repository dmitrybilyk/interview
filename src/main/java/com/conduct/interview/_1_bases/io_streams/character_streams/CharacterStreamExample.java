package com.conduct.interview._1_bases.io_streams.character_streams;

import java.io.*;

public class CharacterStreamExample {
    private static final String FILE_PATH = "/home/dmytro/dev/projects/interview/src/main/java/" +
            "com/conduct/interview/_1_bases/io_streams/" +
            "output.txt";

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(FILE_PATH)));
        int character;
        while ((character = reader.read()) != -1) { // Read one character at a time
            System.out.print((char)character); // Print the character
        }
        reader.close();
//        FileWriter writer = new FileWriter(new File(FILE_PATH));
        BufferedWriter writer = new BufferedWriter(new CharArrayWriter());
        writer.write("hello");
        writer.close();

        InputStream inputStream = new BufferedInputStream(new FileInputStream(FILE_PATH));

        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(FILE_PATH));
//        // Writing to a file using BufferedWriter
//        try (BufferedWriter writer = new BufferedWriter(
//                new FileWriter(
//                        FILE_PATH))) {
//            writer.write("Hello, World!");
//            writer.newLine();
//            writer.write("This is an example of character streams.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // Reading from a file using BufferedReader
//        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
