package com.employeemgmt.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User model class for authentication
 * Matches the user_account and role tables in the database
 */
public class User {
    
    // User roles enum
    public enum UserRole {
        ADMIN("ADMIN"),
        EMPLOYEE("EMPLOYEE");
        
        private final String value;
        
        UserRole(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static UserRole fromString(String value) {
            for (UserRole role : UserRole.values()) {
                if (role.value.equalsIgnoreCase(value)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Unknown role: " + value);
        }
    }
    
    // Fields matching user_account table
    private int userId;
    private Integer empid; // Nullable for admin users
    private String username;
    private String passwordHash;
    private UserRole role;
    private LocalDateTime lastLogin;
    
    // Additional fields for authentication state
    private boolean isLoggedIn;
    private LocalDateTime loginTime;
    
    // Default constructor
    public User() {
        this.isLoggedIn = false;
    }
    
    // Constructor for new user creation
    public User(String username, String passwordHash, UserRole role) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    
    // Constructor for employee users
    public User(String username, String passwordHash, UserRole role, Integer empid) {
        this(username, passwordHash, role);
        this.empid = empid;
    }
    
    // Full constructor (from database)
    public User(int userId, Integer empid, String username, String passwordHash, 
               UserRole role, LocalDateTime lastLogin) {
        this();
        this.userId = userId;
        this.empid = empid;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.lastLogin = lastLogin;
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public Integer getEmpid() { return empid; }
    public void setEmpid(Integer empid) { this.empid = empid; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { 
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username.trim().toLowerCase(); 
    }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { 
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
        this.passwordHash = passwordHash; 
    }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { 
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.role = role; 
    }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public boolean isLoggedIn() { return isLoggedIn; }
    public void setLoggedIn(boolean loggedIn) { 
        this.isLoggedIn = loggedIn;
        if (loggedIn) {
            this.loginTime = LocalDateTime.now();
        } else {
            this.loginTime = null;
        }
    }
    
    public LocalDateTime getLoginTime() { return loginTime; }
    
    // Authentication helper methods
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
    
    public boolean isEmployee() {
        return role == UserRole.EMPLOYEE;
    }
    
    public boolean hasEmployeeRecord() {
        return empid != null && empid > 0;
    }
    
    public String getRoleDisplayName() {
        switch (role) {
            case ADMIN: return "HR Administrator";
            case EMPLOYEE: return "Employee";
            default: return "Unknown";
        }
    }
    
    // Validation method
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
               passwordHash != null && !passwordHash.trim().isEmpty() &&
               role != null &&
               (role == UserRole.ADMIN || (role == UserRole.EMPLOYEE && hasEmployeeRecord()));
    }
    
    // Session management
    public void login() {
        setLoggedIn(true);
        setLastLogin(LocalDateTime.now());
    }
    
    public void logout() {
        setLoggedIn(false);
    }
    
    public boolean isSessionValid(int sessionTimeoutMinutes) {
        if (!isLoggedIn || loginTime == null) {
            return false;
        }
        return loginTime.plusMinutes(sessionTimeoutMinutes).isAfter(LocalDateTime.now());
    }
    
    // Object methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        return userId == user.userId && Objects.equals(username, user.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }
    
    @Override
    public String toString() {
        return String.format("User{userId=%d, username='%s', role=%s, empid=%s, loggedIn=%s}",
                userId, username, role, empid, isLoggedIn);
    }
}