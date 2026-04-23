package com.stmarys.library;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    private static final String DB_URL = "jdbc:sqlite:library.db";

    public BookDAO() {
        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS books (" +
                        "book_id INTEGER PRIMARY KEY, " +
                        "title TEXT NOT NULL, " +
                        "author TEXT NOT NULL, " +
                        "category TEXT, " +
                        "availability_status TEXT" +
                        ")";
                stmt.execute(createTableSQL);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("FATAL ERROR: Could not set up the database.");
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public List<Book> listBooks() throws SQLException {
        List<Book> bookList = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookList.add(new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getString("availability_status")
                ));
            }
        }
        return bookList;
    }

    public List<Book> searchBooks(String searchTerm) throws SQLException {
        List<Book> bookList = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookList.add(new Book(
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("category"),
                            rs.getString("availability_status")
                    ));
                }
            }
        }
        return bookList;
    }
    
    public void addBook(int bookId, String title, String author, String category, String status) throws SQLException {
        String sql = "INSERT INTO books (book_id, title, author, category, availability_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setString(4, category);
            pstmt.setString(5, status);
            pstmt.executeUpdate();
        }
    }

    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, category = ?, availability_status = ? WHERE book_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getCategory());
            pstmt.setString(4, book.getAvailabilityStatus());
            pstmt.setInt(5, book.getBookId());
            pstmt.executeUpdate();
        }
    }

    public void deleteBook(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        }
    }

    public void updateBookStatus(int bookId, String newStatus) throws SQLException {
        String sql = "UPDATE books SET availability_status = ? WHERE book_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        }
    }
}