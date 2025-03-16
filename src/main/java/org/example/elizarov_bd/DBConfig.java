package org.example.elizarov_bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig  {
    private static final String URL = "jdbc:postgresql://localhost:5445/Department";
    private static final String USER = "elizarov";
    private static final String PASSWORD = "1234321895elizarov";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}