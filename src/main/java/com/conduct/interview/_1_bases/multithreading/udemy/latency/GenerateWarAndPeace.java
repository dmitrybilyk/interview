package com.conduct.interview._1_bases.multithreading.udemy.latency;

import java.io.FileWriter;
import java.io.IOException;

public class GenerateWarAndPeace {
    public static void main(String[] args) throws IOException {
        try (FileWriter writer = new FileWriter("/home/dmytro/dev/projects/interview/src/main/java/com/conduct/interview/_1_bases/multithreading/udemy/resources/throughput/war_and_peace.txt")) {
            String sample = "war peace love hate life death freedom slavery hope despair ";
            for (int i = 0; i < 5_000_000; i++) {  // ~50 MB of text
                writer.write(sample);
            }
        }
        System.out.println("war_and_peace.txt generated successfully.");
    }
}
