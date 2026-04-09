package com.stmarys.library;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * This class handles all database operations for the Book entity (CRUD operations).
 * This is our Data Access Object (DAO).
 */
public class BookAccess { // The class name is now BookAccess
    /**
     * Finds all books in the database that match a given title.
     * @param title The title (or part of the title) to search for.
     * @return A list of Book objects that match the search.
     */
    public List<Book> searchBookByTitle(String title) {
        String sql = "SELECT * FROM Books WHERE Title LIKE ?";
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("BookID"),
                    rs.getString("Title"),
                    rs.getString("Author"),
                    rs.getString("Category"),
                    rs.getString("AvailabilityStatus")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return books;
    }
}