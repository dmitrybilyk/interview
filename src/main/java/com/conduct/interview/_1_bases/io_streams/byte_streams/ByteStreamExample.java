package com.conduct.interview._1_bases.io_streams.byte_streams;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ByteStreamExample {
    private static final String FILE_PATH = "/home/dmytro/dev/projects/interview/src/main/java/" +
            "com/conduct/interview/_1_bases/io_streams/" +
            "output.dat";
    public static void main(String[] args) {
        // Writing bytes to a file using FileOutputStream
        try (FileOutputStream outputStream = new FileOutputStream(FILE_PATH)) {
            byte[] data = {65, 66, 67, 68, 69}; // A, B, C, D, E in ASCII
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reading bytes from a file using FileInputStream
        try (FileInputStream inputStream = new FileInputStream(FILE_PATH)) {
            int byteData;
            while ((byteData = inputStream.read()) != -1) {
                System.out.print((char) byteData); // Convert byte to char and print
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
