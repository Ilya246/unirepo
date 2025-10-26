package ru.ssau.tk._AMEBA_._PESEZ_.service;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {
    private final String JDBC_URL;
    private final Properties PROPERTIES = new Properties();
    private Connection connection = null;

    public DatabaseConnection() {
        this("jdbc:postgresql://localhost:5432/function_db");
    }

    public DatabaseConnection(String URL) {
        String user = "postgres"; // placeholder
        String password = "postgres"; // placeholder
        PROPERTIES.setProperty("user", user);
        PROPERTIES.setProperty("password", password);
        Log.info("Connecting to database {} as {}:{}", URL, user, password);
        JDBC_URL = URL;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null) connection = DriverManager.getConnection(JDBC_URL, PROPERTIES);
        return connection;
    }

    public void executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            Log.trace("Executing database update: {}", stmt);
            stmt.executeUpdate();
        }
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        Log.trace("Executing database query: {}", stmt);
        return stmt.executeQuery();
    }
}
