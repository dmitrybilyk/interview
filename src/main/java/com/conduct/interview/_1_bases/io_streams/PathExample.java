package com.conduct.interview._1_bases.io_streams;

import java.nio.file.*;
import java.io.IOException;
import java.util.List;

public class PathExample {
    public static void main(String[] args) {
        Path path = Paths.get("/home/dmytro/dev/projects/interview/src/main/java/com/conduct/interview/_1_bases/io_streams/nio/nio2/output_nio2.txt");
        try {
            List<String> lines = Files.readAllLines(path);
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
