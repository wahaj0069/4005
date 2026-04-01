package com.stmarys.library;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // Import the formatter
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    private static final String DB_URL = "jdbc:sqlite:library.db";

    public BorrowDAO() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS borrowings (" +
                "borrow_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "book_id INTEGER NOT NULL, " +
                "member_id INTEGER NOT NULL, " +
                "borrow_date TEXT NOT NULL, " +
                "due_date TEXT NOT NULL, " +
                "return_date TEXT, " +
                "status TEXT NOT NULL" +
                ")";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            updateSchema(conn);
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("FATAL ERROR: Could not create or update borrowings table.");
            e.printStackTrace();
        }
    }

    private void updateSchema(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, "borrowings", "due_date")) {
            if (!rs.next()) {
                System.out.println("INFO: Old database schema detected. Upgrading 'borrowings' table...");
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE borrowings ADD COLUMN due_date TEXT");
                    stmt.execute("ALTER TABLE borrowings ADD COLUMN status TEXT");
                    stmt.execute("UPDATE borrowings SET status = 'Returned' WHERE return_date IS NOT NULL");
                    stmt.execute("UPDATE borrowings SET status = 'Borrowed' WHERE return_date IS NULL");
                    System.out.println("INFO: Table upgrade successful.");
                }
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    // --- UPDATED: To accept both manual dates ---
    public void borrowBook(int bookId, int memberId, String borrowDateStr, String dueDateStr) throws SQLException {
        String sql = "INSERT INTO borrowings (book_id, member_id, borrow_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
        
        // Define the user-facing format
        DateTimeFormatter userFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Parse the dates from the user's format
        LocalDate borrowDate = LocalDate.parse(borrowDateStr, userFormatter);
        LocalDate dueDate = LocalDate.parse(dueDateStr, userFormatter);

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            // Store in the standard, sortable YYYY-MM-DD format
            pstmt.setString(3, borrowDate.toString());
            pstmt.setString(4, dueDate.toString());
            pstmt.setString(5, "Borrowed");
            pstmt.executeUpdate();
        }
    }

    // (The rest of your BorrowDAO methods: listCurrentBorrowings, returnBook, searchCurrentBorrowings, etc. remain the same as the previous version)
    public List<Borrowing> listCurrentBorrowings() throws SQLException {
        List<Borrowing> borrowingList = new ArrayList<>();
        String sql = "SELECT b.borrow_id, b.book_id, bk.title, m.name, b.borrow_date, b.due_date " +
                     "FROM borrowings b " +
                     "JOIN books bk ON b.book_id = bk.book_id " +
                     "JOIN members m ON b.member_id = m.member_id " +
                     "WHERE b.status = 'Borrowed'"; 

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                borrowingList.add(new Borrowing(
                        rs.getInt("borrow_id"),
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("name"),
                        rs.getString("borrow_date"),
                        rs.getString("due_date")
                ));
            }
        }
        return borrowingList;
    }
    
    public void returnBook(int borrowId) throws SQLException {
        String sql = "UPDATE borrowings SET return_date = ?, status = ? WHERE borrow_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LocalDate.now().toString());
            pstmt.setString(2, "Returned");
            pstmt.setInt(3, borrowId);
            pstmt.executeUpdate();
        }
    }
    
    public List<Borrowing> searchCurrentBorrowings(String searchTerm) throws SQLException {
        List<Borrowing> borrowingList = new ArrayList<>();
        String sql = "SELECT b.borrow_id, b.book_id, bk.title, m.name, b.borrow_date, b.due_date " +
                     "FROM borrowings b " +
                     "JOIN books bk ON b.book_id = bk.book_id " +
                     "JOIN members m ON b.member_id = m.member_id " +
                     "WHERE b.status = 'Borrowed' " +
                     "AND (bk.title LIKE ? OR m.name LIKE ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    borrowingList.add(new Borrowing(
                            rs.getInt("borrow_id"),
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("name"),
                            rs.getString("borrow_date"),
                            rs.getString("due_date")
                    ));
                }
            }
        }
        return borrowingList;
    }
}