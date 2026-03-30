package com.stmarys.library;

// This is a "data-transfer-object". It doesn't represent a database table directly.
// Instead, it's a custom object designed to hold the combined results of our JOIN query.
public class Borrowing {

    private int borrowId;
    private String bookTitle;
    private String memberName;
    private String borrowDate;
    private int bookId; // Keep the book ID handy for the return process

    public Borrowing(int borrowId, int bookId, String bookTitle, String memberName, String borrowDate) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.memberName = memberName;
        this.borrowDate = borrowDate;
    }

    // --- Getters ---
    public int getBorrowId() {
        return borrowId;
    }
    
    public int getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getBorrowDate() {
        return borrowDate;
    }
}