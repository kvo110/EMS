package com.employeemgmt.dao;

import com.employeemgmt.models.User;
import com.employeemgmt.models.User.UserRole;
import com.employeemgmt.utils.SecurityUtils;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User Data Access Object for authentication
 * Handles all database operations related to user authentication and authorization
 */
public class UserDAO {
    
    private DatabaseConnection dbConnection;
    
    // SQL Queries
    private static final String FIND_USER_BY_USERNAME = """
        SELECT ua.user_id, ua.empid, ua.username, ua.password_hash, r.name as role_name, ua.last_login
        FROM user_account ua
        JOIN role r ON ua.role_id = r.role_id
        WHERE ua.username = ?
        """;
    
    private static final String INSERT_USER = """
        INSERT INTO user_account (empid, username, password_hash, role_id)
        VALUES (?, ?, ?, (SELECT role_id FROM role WHERE name = ?))
        """;
    
    private static final String UPDATE_USER = """
        UPDATE user_account 
        SET empid = ?, password_hash = ?, role_id = (SELECT role_id FROM role WHERE name = ?)
        WHERE user_id = ?
        """;
    
    private static final String UPDATE_LAST_LOGIN = """
        UPDATE user_account SET last_login = ? WHERE username = ?
        """;
    
    private static final String CHANGE_PASSWORD = """
        UPDATE user_account SET password_hash = ? WHERE username = ?
        """;
    
    private static final String GET_ALL_USERS = """
        SELECT ua.user_id, ua.empid, ua.username, ua.password_hash, r.name as role_name, ua.last_login
        FROM user_account ua
        JOIN role r ON ua.role_id = r.role_id
        ORDER BY ua.username
        """;
    
    private static final String DELETE_USER = """
        DELETE FROM user_account WHERE user_id = ?
        """;
    
    // Constructor
    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Authenticate user with username and password
     * @param username The username
     * @param password The plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        
        // Sanitize input
        username = SecurityUtils.sanitizeInput(username);
        if (SecurityUtils.containsSQLInjectionPatterns(username)) {
            return null;
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_USER_BY_USERNAME);
            stmt.setString(1, username.toLowerCase());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                
                // Verify password
                if (SecurityUtils.verifyPassword(password, storedHash)) {
                    // Create user object
                    User user = createUserFromResultSet(rs);
                    
                    // Update last login
                    updateLastLogin(username);
                    
                    // Set login state
                    user.login();
                    
                    return user;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return null;
    }
    
    /**
     * Find user by username
     * @param username The username to search for
     * @return User object if found, null otherwise
     */
    public User findByUsername(String username) {
        if (username == null) {
            return null;
        }
        
        username = SecurityUtils.sanitizeInput(username);
        if (SecurityUtils.containsSQLInjectionPatterns(username)) {
            return null;
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_USER_BY_USERNAME);
            stmt.setString(1, username.toLowerCase());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return null;
    }
    
    /**
     * Save a new user to the database
     * @param user The user to save
     * @return true if successful, false otherwise
     */
    public boolean save(User user) {
        if (user == null || !user.isValid()) {
            return false;
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setObject(1, user.getEmpid()); // Can be null for admin users
            stmt.setString(2, user.getUsername().toLowerCase());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().getValue());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated user ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return false;
    }
    
    /**
     * Update an existing user
     * @param user The user to update
     * @return true if successful, false otherwise
     */
    public boolean update(User user) {
        if (user == null || !user.isValid() || user.getUserId() <= 0) {
            return false;
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_USER);
            
            stmt.setObject(1, user.getEmpid());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().getValue());
            stmt.setInt(4, user.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return false;
    }
    
    /**
     * Change user password
     * @param username The username
     * @param newPassword The new plain text password
     * @return true if successful, false otherwise
     */
    public boolean changePassword(String username, String newPassword) {
        if (username == null || newPassword == null) {
            return false;
        }
        
        // Validate password strength
        SecurityUtils.ValidationResult validation = SecurityUtils.validatePasswordStrength(newPassword);
        if (!validation.isValid()) {
            System.err.println("Password validation failed: " + validation.getErrors());
            return false;
        }
        
        // Hash the new password
        String hashedPassword = SecurityUtils.hashPassword(newPassword);
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(CHANGE_PASSWORD);
            
            stmt.setString(1, hashedPassword);
            stmt.setString(2, username.toLowerCase());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return false;
    }
    
    /**
     * Check if user is authorized for a specific operation
     * @param user The user to check
     * @param operation The operation to authorize
     * @return true if authorized, false otherwise
     */
    public boolean isAuthorized(User user, String operation) {
        if (user == null || !user.isLoggedIn() || operation == null) {
            return false;
        }
        
        // Check session validity (30 minutes timeout)
        if (!user.isSessionValid(30)) {
            return false;
        }
        
        // Define authorization rules
        switch (operation.toLowerCase()) {
            case "view_all_employees":
            case "search_all_employees":
            case "add_employee":
            case "update_employee":
            case "delete_employee":
            case "update_salaries":
            case "view_all_reports":
                return user.isAdmin();
                
            case "view_own_data":
            case "view_own_payroll":
                return user.isEmployee() && user.hasEmployeeRecord();
                
            case "login":
            case "logout":
            case "change_own_password":
                return true; // All authenticated users
                
            default:
                return false; // Deny by default
        }
    }
    
    /**
     * Get all users (admin only)
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_ALL_USERS);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return users;
    }
    
    /**
     * Delete a user (admin only)
     * @param userId The user ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_USER);
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return false;
    }
    
    /**
     * Update last login timestamp
     * @param username The username
     */
    private void updateLastLogin(String username) {
        Connection conn = null;
        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_LAST_LOGIN);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, username.toLowerCase());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * Create User object from ResultSet
     * @param rs The ResultSet containing user data
     * @return User object
     * @throws SQLException if database error occurs
     */
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        Integer empid = rs.getObject("empid", Integer.class);
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        UserRole role = UserRole.fromString(rs.getString("role_name"));
        
        Timestamp lastLoginTs = rs.getTimestamp("last_login");
        LocalDateTime lastLogin = lastLoginTs != null ? lastLoginTs.toLocalDateTime() : null;
        
        return new User(userId, empid, username, passwordHash, role, lastLogin);
    }
}
