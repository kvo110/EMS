package com.employeemgmt.services;

import com.employeemgmt.dao.UserDAO;
import com.employeemgmt.models.User;
import com.employeemgmt.models.User.UserRole;
import com.employeemgmt.utils.SecurityUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Service Layer
 * Handles authentication, authorization, and session management business logic
 */
public class AuthenticationService {
    
    private UserDAO userDAO;
    private User currentUser;
    private Map<String, Integer> failedLoginAttempts;
    private Map<String, LocalDateTime> accountLockouts;
    
    // Security constants
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    private static final int SESSION_TIMEOUT_MINUTES = 30;
    
    // Constructor
    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.failedLoginAttempts = new HashMap<>();
        this.accountLockouts = new HashMap<>();
    }
    
    /**
     * Authenticate user and establish session
     * @param username The username
     * @param password The password
     * @return AuthenticationResult with success status and user info
     */
    public AuthenticationResult login(String username, String password) {
        AuthenticationResult result = new AuthenticationResult();
        
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            result.setSuccess(false);
            result.setMessage("Username cannot be empty");
            return result;
        }
        
        if (password == null || password.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("Password cannot be empty");
            return result;
        }
        
        username = username.trim().toLowerCase();
        
        // Check if account is locked
        if (isAccountLocked(username)) {
            result.setSuccess(false);
            result.setMessage("Account is temporarily locked due to multiple failed login attempts. Please try again later.");
            return result;
        }
        
        // Attempt authentication
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            // Successful login
            this.currentUser = user;
            clearFailedAttempts(username);
            
            result.setSuccess(true);
            result.setMessage("Login successful");
            result.setUser(user);
            
            // Log successful login
            logSecurityEvent("LOGIN_SUCCESS", username, "User logged in successfully");
            
        } else {
            // Failed login
            recordFailedAttempt(username);
            
            result.setSuccess(false);
            result.setMessage("Invalid username or password");
            
            // Log failed login
            logSecurityEvent("LOGIN_FAILED", username, "Invalid credentials provided");
        }
        
        return result;
    }
    
    /**
     * Logout current user
     * @return true if successful, false otherwise
     */
    public boolean logout() {
        if (currentUser != null) {
            String username = currentUser.getUsername();
            currentUser.logout();
            currentUser = null;
            
            // Log logout
            logSecurityEvent("LOGOUT", username, "User logged out");
            return true;
        }
        return false;
    }
    
    /**
     * Check if user is authorized for a specific operation
     * @param operation The operation to check
     * @return true if authorized, false otherwise
     */
    public boolean isAuthorized(String operation) {
        if (currentUser == null) {
            return false;
        }
        
        return userDAO.isAuthorized(currentUser, operation);
    }
    
    /**
     * Get current logged-in user
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        if (currentUser != null && !currentUser.isSessionValid(SESSION_TIMEOUT_MINUTES)) {
            // Session expired
            logSecurityEvent("SESSION_EXPIRED", currentUser.getUsername(), "Session expired");
            currentUser.logout();
            currentUser = null;
        }
        
        return currentUser;
    }
    
    /**
     * Validate current session
     * @return true if session is valid, false otherwise
     */
    public boolean validateSession() {
        User user = getCurrentUser(); // This also checks session validity
        return user != null && user.isLoggedIn();
    }
    
    /**
     * Change password for current user
     * @param oldPassword The current password
     * @param newPassword The new password
     * @return PasswordChangeResult with success status and message
     */
    public PasswordChangeResult changePassword(String oldPassword, String newPassword) {
        PasswordChangeResult result = new PasswordChangeResult();
        
        if (currentUser == null) {
            result.setSuccess(false);
            result.setMessage("No user logged in");
            return result;
        }
        
        // Validate old password
        if (!SecurityUtils.verifyPassword(oldPassword, currentUser.getPasswordHash())) {
            result.setSuccess(false);
            result.setMessage("Current password is incorrect");
            return result;
        }
        
        // Validate new password strength
        SecurityUtils.ValidationResult validation = SecurityUtils.validatePasswordStrength(newPassword);
        if (!validation.isValid()) {
            result.setSuccess(false);
            result.setMessage("Password validation failed: " + validation.getErrors());
            return result;
        }
        
        // Change password
        boolean success = userDAO.changePassword(currentUser.getUsername(), newPassword);
        
        if (success) {
            // Update current user's password hash
            currentUser.setPasswordHash(SecurityUtils.hashPassword(newPassword));
            
            result.setSuccess(true);
            result.setMessage("Password changed successfully");
            
            logSecurityEvent("PASSWORD_CHANGED", currentUser.getUsername(), "Password changed successfully");
        } else {
            result.setSuccess(false);
            result.setMessage("Failed to change password. Please try again.");
        }
        
        return result;
    }
    
    /**
     * Create a new user account (admin only)
     * @param username The username
     * @param password The password
     * @param role The user role
     * @param empid The employee ID (for employee users)
     * @return UserCreationResult with success status and message
     */
    public UserCreationResult createUser(String username, String password, UserRole role, Integer empid) {
        UserCreationResult result = new UserCreationResult();
        
        // Temporary: allow user creation for setup
        if (currentUser == null) {
            System.out.println("[DEV MODE] Allowing user creation without login (setup phase)");
        } else if (!isAuthorized("add_employee")) {
            result.setSuccess(false);
            result.setMessage("Insufficient permissions to create user accounts");
            return result;
        }
        
        // Validate username
        SecurityUtils.ValidationResult usernameValidation = SecurityUtils.validateUsername(username);
        if (!usernameValidation.isValid()) {
            result.setSuccess(false);
            result.setMessage("Username validation failed: " + usernameValidation.getErrors());
            return result;
        }
        
        // Check if username already exists
        if (userDAO.findByUsername(username) != null) {
            result.setSuccess(false);
            result.setMessage("Username already exists");
            return result;
        }
        
        // Validate password
        SecurityUtils.ValidationResult passwordValidation = SecurityUtils.validatePasswordStrength(password);
        if (!passwordValidation.isValid()) {
            result.setSuccess(false);
            result.setMessage("Password validation failed: " + passwordValidation.getErrors());
            return result;
        }
        
        // Create user
        String hashedPassword = SecurityUtils.hashPassword(password);
        User newUser = new User(username, hashedPassword, role, empid);
        
        boolean success = userDAO.save(newUser);
        
        if (success) {
            result.setSuccess(true);
            result.setMessage("User account created successfully");
            result.setUser(newUser);
            
            logSecurityEvent("USER_CREATED", currentUser.getUsername(), 
                           "Created new user account: " + username);
        } else {
            result.setSuccess(false);
            result.setMessage("Failed to create user account");
        }
        
        return result;
    }
    
    /**
     * Check if account is locked due to failed login attempts
     * @param username The username to check
     * @return true if account is locked, false otherwise
     */
    private boolean isAccountLocked(String username) {
        LocalDateTime lockoutTime = accountLockouts.get(username);
        if (lockoutTime != null) {
            if (lockoutTime.plusMinutes(LOCKOUT_DURATION_MINUTES).isAfter(LocalDateTime.now())) {
                return true; // Still locked
            } else {
                // Lockout expired
                accountLockouts.remove(username);
                clearFailedAttempts(username);
            }
        }
        return false;
    }
    
    /**
     * Record a failed login attempt
     * @param username The username
     */
    private void recordFailedAttempt(String username) {
        int attempts = failedLoginAttempts.getOrDefault(username, 0) + 1;
        failedLoginAttempts.put(username, attempts);
        
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            // Lock the account
            accountLockouts.put(username, LocalDateTime.now());
            logSecurityEvent("ACCOUNT_LOCKED", username, 
                           "Account locked due to " + attempts + " failed login attempts");
        }
    }
    
    /**
     * Clear failed login attempts for a user
     * @param username The username
     */
    private void clearFailedAttempts(String username) {
        failedLoginAttempts.remove(username);
        accountLockouts.remove(username);
    }
    
    /**
     * Log security events (in production, this would write to a security log)
     * @param event The event type
     * @param username The username involved
     * @param details Additional details
     */
    private void logSecurityEvent(String event, String username, String details) {
        LocalDateTime timestamp = LocalDateTime.now();
        System.out.printf("[SECURITY] %s - %s - %s - %s%n", 
                         timestamp, event, username, details);
    }
    
    /**
     * Result class for authentication operations
     */
    public static class AuthenticationResult {
        private boolean success;
        private String message;
        private User user;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }
    
    /**
     * Result class for password change operations
     */
    public static class PasswordChangeResult {
        private boolean success;
        private String message;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * Result class for user creation operations
     */
    public static class UserCreationResult {
        private boolean success;
        private String message;
        private User user;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }
}
