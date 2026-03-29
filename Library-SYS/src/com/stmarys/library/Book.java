package com.stmarys.library;

/**
 * This class is a blueprint for a Book object. It holds the data for one book.
 */
public class Book {

    private int bookId;
    private String title;
    private String author;
    private String category;
    private String availabilityStatus;

    // This is the constructor. It's used to create a new Book.
    public Book(int bookId, String title, String author, String category, String availabilityStatus) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.availabilityStatus = availabilityStatus;
    }

    // These are "getters" to read the data from a Book object.
    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    // This is a "setter" to change the data in a Book object.
    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}