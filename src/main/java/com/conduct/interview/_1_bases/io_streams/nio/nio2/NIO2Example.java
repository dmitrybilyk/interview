package com.conduct.interview._1_bases.io_streams.nio.nio2;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class NIO2Example {
    private static final String FILE_PATH = "/home/dmytro/dev/projects/interview/src/main/java/" +
            "com/conduct/interview/_1_bases/io_streams/nio/nio2/" +
            "output_nio2.txt";
    public static void main(String[] args) {
        Path path = Paths.get(FILE_PATH);
        try {
            String content = Files.readString(path);
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
