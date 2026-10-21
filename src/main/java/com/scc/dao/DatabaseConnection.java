package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Opens connections to the SQLite file scenario.db (created in the project folder)
 * and makes sure the table exists. All JDBC setup lives here, in the DAO layer.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:scenario.db";

    private DatabaseConnection() {
    }

    /** Opens a new connection with foreign keys switched on. */
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");
        }
        return connection;
    }

    /** Creates the table the first time the program runs. */
    public static void initialize() throws SQLException {
        // AUTOINCREMENT makes the id start at 1 and count up by itself.
        String sql = """
                CREATE TABLE IF NOT EXISTS patient_treatments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_name TEXT NOT NULL,
                    treatment TEXT NOT NULL,
                    amount_due REAL NOT NULL,
                    payment TEXT NOT NULL
                )
                """;
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
