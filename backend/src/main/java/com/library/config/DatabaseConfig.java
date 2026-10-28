package com.library.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager using a simple connection-per-request pattern.
 * For production scaling, swap this with a HikariCP connection pool.
 */
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    static {
        try {
            Class.forName(AppConfig.getDbDriver());
            logger.info("MySQL JDBC driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC driver not found!", e);
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
    }

    private DatabaseConfig() {}

    /**
     * Returns a new database connection.
     * The caller is responsible for closing the connection (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        String url      = AppConfig.getDbUrl();
        String username = AppConfig.getDbUsername();
        String password = AppConfig.getDbPassword();

        if (url == null || url.isEmpty()) {
            throw new SQLException("Database URL is not configured in application.properties");
        }

        Connection conn = DriverManager.getConnection(url, username, password);
        conn.setAutoCommit(true);
        return conn;
    }

    /**
     * Returns a connection with auto-commit disabled (for transactional operations).
     */
    public static Connection getTransactionalConnection() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * Safely closes a connection (null-safe).
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Rolls back a transactional connection safely.
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.error("Error rolling back transaction", e);
            }
        }
    }

    /**
     * Tests database connectivity. Used at startup.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.error("Database connection test failed: {}", e.getMessage());
            return false;
        }
    }
}
