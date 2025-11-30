package com.employeemgmt.dao;

import com.employeemgmt.models.User;
import com.employeemgmt.models.User.UserRole;
import com.employeemgmt.utils.SecurityUtils;
import java.sql.*;
import java.time.LocalDateTime;

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

    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    // ========= AUTHENTICATION =========
    public User authenticate(String username, String password) {
        if (username == null || password == null) return null;

        username = SecurityUtils.sanitizeInput(username);

        if (SecurityUtils.containsSQLInjectionPatterns(username)) {
            System.err.println("[SECURITY] SQL Injection detected in username.");
            return null;
        }

        Connection conn = null;

        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_USER_BY_USERNAME);
            stmt.setString(1, username.toLowerCase());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");

                if (SecurityUtils.verifyPassword(password, hash)) {
                    User user = createUserFromResultSet(rs);
                    updateLastLogin(username);
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

    // ========= FIND USER =========
    public User findByUsername(String username) {
        if (username == null) return null;

        username = SecurityUtils.sanitizeInput(username);
        if (SecurityUtils.containsSQLInjectionPatterns(username)) return null;

        Connection conn = null;

        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_USER_BY_USERNAME);
            stmt.setString(1, username.toLowerCase());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return createUserFromResultSet(rs);

        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return null;
    }

    // ========= CREATE USER =========
    public boolean save(User user) {
        if (user == null || !user.isValid()) return false;

        Connection conn = null;

        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt =
                    conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);

            stmt.setObject(1, user.getEmpid());
            stmt.setString(2, user.getUsername().toLowerCase());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().getValue());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) user.setUserId(keys.getInt(1));
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return false;
    }

    // ========= PASSWORD CHANGE =========
    public boolean changePassword(String username, String newPass) {
        if (username == null || newPass == null) return false;

        var validation = SecurityUtils.validatePasswordStrength(newPass);
        if (!validation.isValid()) {
            System.err.println("Password too weak: " + validation.getErrors());
            return false;
        }

        String newHash = SecurityUtils.hashPassword(newPass);

        Connection conn = null;

        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(CHANGE_PASSWORD);

            stmt.setString(1, newHash);
            stmt.setString(2, username.toLowerCase());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Password update error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return false;
    }

    // ========= BUILD USER OBJECT =========
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        Integer empid = rs.getObject("empid", Integer.class);
        String username = rs.getString("username");
        String hash = rs.getString("password_hash");
        UserRole role = UserRole.fromString(rs.getString("role_name"));

        Timestamp ts = rs.getTimestamp("last_login");
        LocalDateTime lastLogin =
                (ts != null ? ts.toLocalDateTime() : null);

        return new User(id, empid, username, hash, role, lastLogin);
    }

    // ========= LAST LOGIN =========
    private void updateLastLogin(String username) {
        Connection conn = null;

        try {
            conn = dbConnection.getAdminConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_LAST_LOGIN);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, username.toLowerCase());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to update last login: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
}