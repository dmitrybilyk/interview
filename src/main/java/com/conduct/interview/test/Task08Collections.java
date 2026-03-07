package com.conduct.interview.test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* Implement collections logic as described.
*/
public class Task08Collections {

    public static void main(String[] args) {
        Map<String, List<Student>> studentsByLanguage = getStudentsByLanguage(students);
        getAllLanguages(students).forEach(System.out::println);
    }

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

    // Data example
    static final List<Student> students = Arrays.asList(
            new Student("Doug Lea", Arrays.asList("Java", "C#", "JavaScript")),
            new Student("Bjarne Stroustrup", Arrays.asList("C", "C++", "Java")),
            new Student("Martin Odersky", Arrays.asList("Java", "Scala", "Smalltalk"))
    );

    /**
     * Given list of students group them by language.
     */
    public static Map<String, List<Student>> getStudentsByLanguage(List<Student> students) {
        Map<String, List<Student>> collect3 = students.stream()
                .flatMap(student -> student.getLanguages().stream()
                        .map(lang -> new AbstractMap.SimpleEntry<>(lang, student)))
                .collect(Collectors.groupingBy(s -> s.getKey(), Collectors.mapping(o -> o.getValue(), Collectors.toList())));


        Map<String, List<Student>> studentsByLanguage = new HashMap<>();

//        for(Student student : students) {
//            for(String language : student.getLanguages()) {
//                List<Student> studentsByLanguages = studentsByLanguage.getOrDefault(language, new ArrayList<>());
//                studentsByLanguages.add(student);
//                studentsByLanguage.put(language, studentsByLanguages);
//            }
//        }

        Map<String, List<Student>> collect = students.stream()
                .flatMap(student -> student.getLanguages().stream()
                .map(lang -> new AbstractMap.SimpleEntry<>(lang, student)))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

//        List<String> listStream = students.stream()
//        List<AbstractMap.SimpleEntry<String, Student>> list = students.stream()
//        Stream<AbstractMap.SimpleEntry<String, Student>> simpleEntryStream = students.stream()
        Map<String, List<Student>> collect2 = students.stream()
//                .flatMap(student -> student.getLanguages().stream()).toList();
                .flatMap(student -> student.getLanguages().stream()
                        .map(lang -> new AbstractMap.SimpleEntry<>(lang, student)))
                .collect(Collectors.groupingBy(o -> o.getKey(),
                        Collectors.mapping(o -> o.getValue(), Collectors.toList())));
//                        )).toList();

        Map<String, List<Student>> collect1 = students.stream()
                .flatMap(student -> student.getLanguages().stream()
                        .map(lang -> new AbstractMap.SimpleEntry<>(lang, student)))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        return collect;
    }

    /**
     * Given list of students with languages, return list of unique languages.
     */
    public static List<String> getAllLanguages(List<Student> students) {
        return students.stream().flatMap(student -> student.getLanguages().stream()).distinct().collect(Collectors.toList());
    }

    /**
     * Given list of students, remove duplicates by name.
    public static List<Student> removeDuplicates(List<Student> students) {
        throw new UnsupportedOperationException();
    }
    */

}