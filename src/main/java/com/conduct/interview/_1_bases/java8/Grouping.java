package com.conduct.interview._1_bases.java8;


import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Grouping {
    public static void main(String[] args) {
        Map<String, List<Student>> langsOfStudents = getLangsOfStudents(students);
        System.out.println(langsOfStudents);
    }

    private static Map<String, List<Student>> getLangsOfStudents(List<Student> students) {
//        return students.stream()
//                .flatMap(student -> student.getLanguages().stream()
//                        .map(lang -> new AbstractMap.SimpleEntry<>(lang, student)))
//                .collect(Collectors.groupingBy(
//                        stringStudentSimpleEntry1 -> stringStudentSimpleEntry1.getKey(), Collectors.mapping(stringStudentSimpleEntry -> stringStudentSimpleEntry.getValue(), Collectors.toList())
//                        ));
        return students.stream()
                .flatMap(student -> student.getLanguages().stream()
                        .map(lang -> new AbstractMap.SimpleEntry<>(lang, student)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                        stringStudentSimpleEntry -> List.of(stringStudentSimpleEntry.getValue()),
                        (studentsSource, students2) -> {
                            ArrayList<Student> result = new ArrayList<>(studentsSource);
                            result.addAll(students2);
                            return result;
                }));
    }

    static final List<Student> students = Arrays.asList(
            new Student("Doug Lea", Arrays.asList("Java", "C#", "JavaScript")),
            new Student("Bjarne Stroustrup", Arrays.asList("C", "C++", "Java")),
            new Student("Martin Odersky", Arrays.asList("Java", "Scala", "Smalltalk"))
    );

    static class Student {
        private String name;
        private List<String> languages;

        Student(String name, List<String> languages) {
            this.name = name;
            this.languages = languages;
        }

        public String getName() {
            return name;
        }

        public List<String> getLanguages() {
            return languages;
        }

    }
}
