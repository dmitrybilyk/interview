package com.conduct.interview._5_solid._1_single_responsibility.breaking_principle;

// Class that handles both book data and printing
class Book {
    private String title;
    private String author;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    // Method to print book details
    public void printBookDetails() {
        System.out.println("Title: " + title + ", Author: " + author);
    }
}
