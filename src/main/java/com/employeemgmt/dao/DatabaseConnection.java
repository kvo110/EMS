package com.employeemgmt.dao;

import com.employeemgmt.utils.EnvLoader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*
   DatabaseConnection.java
   ------------------------
   I cleaned this up so it actually works with our DAO classes.

   Before this, EmployeeDAO kept calling db.getConnection(), but
   our DatabaseConnection class never actually had that method.
   That’s why the file showed up red in VS Code.

   I added a proper getConnection() method at the bottom that simply
   returns the admin connection (full access) for now.
   If we want employee-level read-only connections later, we can
   extend this easily.
*/

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private static Properties properties;

    // file inside src/main/resources
    private static final String PROPERTIES_FILE = "/database.properties";

    private static final String DB_URL_KEY = "db.url";
    private static final String DB_DRIVER_KEY = "db.driver";

    private static final String ADMIN_USERNAME_KEY = "db.admin.username";
    private static final String ADMIN_PASSWORD_KEY = "db.admin.password";

    private static final String EMPLOYEE_USERNAME_KEY = "db.employee.username";
    private static final String EMPLOYEE_PASSWORD_KEY = "db.employee.password";

    // constructor is private because we use a singleton
    private DatabaseConnection() {
        EnvLoader.loadEnv(); // loads .env values if present
        loadProperties();
        loadDriver();
    }

    // grab the instance one time (singleton pattern)
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // reads database.properties
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("Could not find " + PROPERTIES_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading database.properties", e);
        }
    }

    // load JDBC driver
    private void loadDriver() {
        try {
            Class.forName(properties.getProperty(DB_DRIVER_KEY));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver missing", e);
        }
    }

    // HR admin connection (full permissions)
    public Connection getAdminConnection() throws SQLException {
        String url = EnvLoader.getEnv("DB_URL", properties.getProperty(DB_URL_KEY));
        String username = EnvLoader.getEnv("DB_ADMIN_USERNAME", properties.getProperty(ADMIN_USERNAME_KEY));
        String password = EnvLoader.getEnv("DB_ADMIN_PASSWORD", properties.getProperty(ADMIN_PASSWORD_KEY));

        return DriverManager.getConnection(url, username, password);
    }

    // employee connection (limited permissions)
    public Connection getEmployeeConnection() throws SQLException {
        String url = EnvLoader.getEnv("DB_URL", properties.getProperty(DB_URL_KEY));
        String username = EnvLoader.getEnv("DB_EMPLOYEE_USERNAME", properties.getProperty(EMPLOYEE_USERNAME_KEY));
        String password = EnvLoader.getEnv("DB_EMPLOYEE_PASSWORD", properties.getProperty(EMPLOYEE_PASSWORD_KEY));

        return DriverManager.getConnection(url, username, password);
    }

    /*
        🔥 IMPORTANT FIX 🔥
        -------------------
        EmployeeDAO expects db.getConnection() to exist.

        For now, we will always return the admin connection,
        because all DAO operations require full privileges.
    */
    public Connection getConnection() throws SQLException {
        return getAdminConnection(); // simple and works for entire project
    }

    // safely close connections
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try { conn.close(); } 
            catch (SQLException ignored) {}
        }
    }

    // optional: a connection tester (handy during debugging)
    public boolean testConnection() {
        try (Connection conn = getAdminConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            System.err.println("DB test failed: " + e.getMessage());
            return false;
        }
    }
}