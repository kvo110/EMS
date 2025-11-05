package com.employeemgmt.models;

import java.util.Objects;

/**
 * User model class for authentication
 * 
 * Maps to user_account table:
 * - username
 * - password (hashed)
 * - role (HR_ADMIN, GENERAL_EMPLOYEE)
 * - empid (link to Employee if general employee)
 * - isActive
 */
public class User {
    
    /**
     * User role enum for role-based access control
     */
    public enum UserRole {
        HR_ADMIN("ADMIN", "HR Admin - Full access to all reports and data"),
        GENERAL_EMPLOYEE("EMPLOYEE", "General Employee - Limited access to own data only");
        
        private final String dbValue;
        private final String description;
        
        UserRole(String dbValue, String description) {
            this.dbValue = dbValue;
            this.description = description;
        }
        
        public String getDbValue() {
            return dbValue;
        }
        
        public String getDescription() {
            return description;
        }
        
        /**
         * Convert database role value to UserRole enum
         */
        public static UserRole fromDbValue(String dbValue) {
            if (dbValue == null) {
                return GENERAL_EMPLOYEE;
            }
            switch (dbValue.toUpperCase()) {
                case "ADMIN":
                    return HR_ADMIN;
                case "EMPLOYEE":
                    return GENERAL_EMPLOYEE;
                default:
                    return GENERAL_EMPLOYEE;
            }
        }
    }
    
    private int userId;
    private Integer empid; // Can be null for admin users
    private String username;
    private String passwordHash;
    private UserRole role;
    private boolean isActive;
    
    // Default constructor
    public User() {
        this.role = UserRole.GENERAL_EMPLOYEE;
        this.isActive = true;
    }
    
    // Constructor with all fields
    public User(int userId, Integer empid, String username, String passwordHash, UserRole role, boolean isActive) {
        this.userId = userId;
        this.empid = empid;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = isActive;
    }
    
    // Constructor for new user
    public User(String username, String passwordHash, UserRole role, Integer empid) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.empid = empid;
        this.isActive = true;
    }
    
    // Check if user has HR Admin role
    public boolean isHRAdmin() {
        return role == UserRole.HR_ADMIN;
    }
    
    // Check if user has General Employee role
    public boolean isGeneralEmployee() {
        return role == UserRole.GENERAL_EMPLOYEE;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public Integer getEmpid() {
        return empid;
    }
    
    public void setEmpid(Integer empid) {
        this.empid = empid;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", empid=" + empid +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && Objects.equals(username, user.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }
}
