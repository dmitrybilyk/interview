package com.conduct.interview._1_bases.java8.streams.groupingBy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Modern Stream transformation: Grouping Students by Language
 * using flatMap and AbstractMap.SimpleEntry.
 */
public class Task08Collections {

    public static void main(String[] args) {
        // Data Initialization
        List<Student> students = Arrays.asList(
                new Student("Doug Lea", Arrays.asList("Java", "C#", "JavaScript")),
                new Student("Bjarne Stroustrup", Arrays.asList("C", "C++", "Java")),
                new Student("Martin Odersky", Arrays.asList("Java", "Scala", "Smalltalk"))
        );

        // Perform Transformation
        Map<String, List<Student>> studentsByLanguage = transform(students);

        // Beautiful Output
        System.out.println("=== Students Grouped by Language ===");
        studentsByLanguage.forEach((language, studentList) -> {
            System.out.printf("%-12s: %s%n", language, studentList);
        });
    }

    /**
     * The Transformation Logic
     * 1. flatMap: Flattens the nested lists of languages.
     * 2. SimpleEntry: Pairs each language with its specific student.
     * 3. groupingBy: Aggregates students back into lists based on the language key.
     */
    public static Map<String, List<Student>> transform(List<Student> students) {
        return students.stream()
                .flatMap(student -> student.getLanguages().stream()
                        .map(language -> new AbstractMap.SimpleEntry<>(language, student)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }

    // --- Domain Class ---
    static class Student {
        private final String name;
        private final List<String> languages;

        Student(String name, List<String> languages) {
            this.name = name;
            this.languages = languages;
        }

        public String getName() { return name; }
        public List<String> getLanguages() { return languages; }

        @Override
        public String toString() {
            return name; // Simplified for clean console output
        }
    }
}