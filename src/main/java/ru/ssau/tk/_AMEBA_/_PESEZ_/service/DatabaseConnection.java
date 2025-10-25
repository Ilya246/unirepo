package ru.ssau.tk._AMEBA_._PESEZ_.service;

import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/function_db";
    private static final Properties PROPERTIES = new Properties();
    private static Connection connection = null;

    static {
        PROPERTIES.setProperty("user", "postgres");
        PROPERTIES.setProperty("password", "postgres");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Не найден PostgreSQL JDBC Driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null) connection = DriverManager.getConnection(JDBC_URL, PROPERTIES);
        return connection;
    }

    public static void executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        }
    }

    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeQuery();
        }
    }
}
