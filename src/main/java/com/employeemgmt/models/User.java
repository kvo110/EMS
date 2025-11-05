package com.employeemgmt.models;

/**
 * User model class for authentication
 * 
 * TODO: Implement User entity for login system
 * - username
 * - password (hashed)
 * - role (HR_ADMIN, GENERAL_EMPLOYEE)
 * - empid (link to Employee if general employee)
 * - isActive
 * 
 * Requirements:
 * - Support role-based access control
 * - Secure password handling (never store plain text)
 * - Link general employees to their employee record
 */
public class User {
    
    // TODO: Add enum for UserRole (HR_ADMIN, GENERAL_EMPLOYEE)
    public enum UserRole {
        HR_ADMIN,
        GENERAL_EMPLOYEE
    }
    
    // TODO: Add private fields for user attributes
    private int userId;
    private String username;
    private String password;
    private UserRole role;
    private int empId;
    private boolean isActive;
    
    // TODO: Add constructors
    public User() {
        this.isActive = true;
    }

    public User(String username, String password, UserRole role, int empId, boolean isActive) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.empId = empId;
        this.isActive = isActive;
    }
    
    // TODO: Add getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // TODO: Add authentication methods
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    // TODO: Override Object methods
    @Override
    public String toString() {
        return String.format("User{username='%s', role=%s, active=%s}", username, role, isActive);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        return username.equals(other.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}