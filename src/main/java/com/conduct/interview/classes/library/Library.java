package com.conduct.interview.classes.library;

import com.conduct.interview.classes.library.exception.BookNotFoundException;
import com.conduct.interview.classes.library.exception.MemberNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class Library {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, LibraryMember> members = new HashMap<>();

    public void addBook(Book book) {
        books.put(book.getISBN(), book);
    }

    public Book borrowBook(String ISBN, String memberId) {
        LibraryMember libraryMember = members.entrySet()
                .stream().filter(entry -> entry.getKey().equals(memberId))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new MemberNotFoundException("Member " + memberId + "is not found"));

        Book book = books.entrySet()
                .stream().filter(stringBookEntry -> stringBookEntry.getKey().equals(ISBN))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book " + ISBN + "is not found"));

        book.setAvailable(false);
        libraryMember.addBook(book);
        return books.get(ISBN);
    }

    public void returnBook(String ISBN, Book book) {
        books.put(ISBN, book);
    }

    public void displayBooks() {
        books.values().forEach(System.out::println);
    }

    public void addMember(LibraryMember member) {
        this.members.put(member.getMemberId(), member);
    }

    public LibraryMember getMember(String memberId) {
        return members.get(memberId);
    }

}
