package com.stmarys.library;

public class Borrowing {

    private int borrowId;
    private String bookTitle;
    private String memberName;
    private String borrowDate;
    private int bookId;
    private String dueDate;

    public Borrowing(int borrowId, int bookId, String bookTitle, String memberName, String borrowDate, String dueDate) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.memberName = memberName;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public int getBorrowId() { return borrowId; }
    public int getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getMemberName() { return memberName; }
    public String getBorrowDate() { return borrowDate; }

    public String getDueDate() {
        return dueDate;
    }
}