package com.stmarys.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    
    private static final String DB_URL = "jdbc:sqlite:library.db";
    
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}