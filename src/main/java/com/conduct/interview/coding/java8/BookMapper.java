package com.conduct.interview.coding.java8;

import java.util.*;
import java.util.stream.Collectors;

class Book {
    String title;
    String author;

    Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String getAuthor() { return author; }
    public String getTitle() { return title; }
}

public class BookMapper {
    public static void main(String[] args) {
        List<Book> books = Arrays.asList(
            new Book("The Hobbit", "J.R.R. Tolkien"),
            new Book("The Shining", "Stephen King"),
            new Book("The Silmarillion", "J.R.R. Tolkien"),
            new Book("Misery", "Stephen King")
        );

        // Logic: Grouping books by author name
        Map<String, List<String>> booksByAuthor = books.stream()
            .collect(Collectors.groupingBy(
                Book::getAuthor, 
                Collectors.mapping(Book::getTitle, Collectors.toList())
            ));

        System.out.println(booksByAuthor);
    }
}