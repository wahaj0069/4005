package com.stmarys.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // Corrected URL to match your actual database filename.
    private static final String DB_URL = "jdbc:sqlite:datalibrary.db";
    /**
     * This is the method that actually connects to the database.
     */
    public static Connection connect() throws SQLException {
        // This line uses the JDBC driver to connect to the file specified in the URL.
        return DriverManager.getConnection(DB_URL);
    }
    
}
