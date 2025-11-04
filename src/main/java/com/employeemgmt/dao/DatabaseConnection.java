package com.employeemgmt.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.employeemgmt.utils.EnvLoader;

/**
 * Database Connection utility class
 * Manages JDBC connections to MySQL database
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private static Properties properties;
    
    // Database configuration constants
    private static final String PROPERTIES_FILE = "/database.properties";
    private static final String DB_URL_KEY = "db.url";
    private static final String DB_DRIVER_KEY = "db.driver";
    private static final String ADMIN_USERNAME_KEY = "db.admin.username";
    private static final String ADMIN_PASSWORD_KEY = "db.admin.password";
    private static final String EMPLOYEE_USERNAME_KEY = "db.employee.username";
    private static final String EMPLOYEE_PASSWORD_KEY = "db.employee.password";
    
    // Private constructor for singleton pattern
    private DatabaseConnection() {
        // Load environment variables first
        EnvLoader.loadEnv();
        loadProperties();
        loadDriver();
    }
    
    // Singleton pattern implementation
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    // Load database properties from file
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + PROPERTIES_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading database properties", e);
        }
    }
    
    // Load MySQL JDBC driver
    private void loadDriver() {
        try {
            Class.forName(properties.getProperty(DB_DRIVER_KEY));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }
    
    // Get connection for HR Admin (full access)
    public Connection getAdminConnection() throws SQLException {
        // Try environment variables first, fallback to properties file
        String url = EnvLoader.getEnv("DB_URL", properties.getProperty(DB_URL_KEY));
        String username = EnvLoader.getEnv("DB_ADMIN_USERNAME", properties.getProperty(ADMIN_USERNAME_KEY));
        String password = EnvLoader.getEnv("DB_ADMIN_PASSWORD", properties.getProperty(ADMIN_PASSWORD_KEY));
        
        return DriverManager.getConnection(url, username, password);
    }
    
    // Get connection for General Employee (read-only access)
    public Connection getEmployeeConnection() throws SQLException {
        // Try environment variables first, fallback to properties file
        String url = EnvLoader.getEnv("DB_URL", properties.getProperty(DB_URL_KEY));
        String username = EnvLoader.getEnv("DB_EMPLOYEE_USERNAME", properties.getProperty(EMPLOYEE_USERNAME_KEY));
        String password = EnvLoader.getEnv("DB_EMPLOYEE_PASSWORD", properties.getProperty(EMPLOYEE_PASSWORD_KEY));
        
        return DriverManager.getConnection(url, username, password);
    }
    
    // Test database connection
    public boolean testConnection() {
        try (Connection conn = getAdminConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    // Close connection safely
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
