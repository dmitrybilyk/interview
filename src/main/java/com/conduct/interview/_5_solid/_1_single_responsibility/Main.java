package com.conduct.interview._5_solid._1_single_responsibility;

// Class representing a Book
class Book {
  private String title;
  private String author;

  // Constructor
  public Book(String title, String author) {
    this.title = title;
    this.author = author;
  }

  // Getter methods
  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  // Method to get a book description
  public String getBookDetails() {
    return "Title: " + title + ", Author: " + author;
  }
}

// Class handling printing logic
class PrintSomething {
  // Method to print any object
  public void print(Object toPrint) {
    System.out.println(toPrint.toString());
  }
}

public class Main {
  public static void main(String[] args) {
    Book book = new Book("The Great Gatsby", "F. Scott Fitzgerald");
    PrintSomething printer = new PrintSomething();

    // Using PrintSomething to print book details
    printer.print(book.getBookDetails());
  }
}
