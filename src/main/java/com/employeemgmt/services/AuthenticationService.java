package com.employeemgmt.services;

import com.employeemgmt.dao.UserDAO;
import com.employeemgmt.models.User;
import com.employeemgmt.models.User.UserRole;
import com.employeemgmt.utils.SecurityUtils;

public class AuthenticationService {

    private final UserDAO userDAO = new UserDAO();

    public static class AuthenticationResult {
        private final boolean success;
        private final String message;
        private final User user;

        public AuthenticationResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }

    public static class UserCreationResult {
        private final boolean success;
        private final String message;

        public UserCreationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    // ----------------------------
    // Login logic
    // ----------------------------
    public AuthenticationResult login(String username, String password) {

        if (username == null || username.isBlank()) {
            return new AuthenticationResult(false, "Username cannot be empty.", null);
        }

        if (password == null || password.isBlank()) {
            return new AuthenticationResult(false, "Password cannot be empty.", null);
        }

        String normalized = username.trim().toLowerCase();
        User user = userDAO.authenticate(normalized, password);

        if (user == null) {
            return new AuthenticationResult(false, "Invalid username or password.", null);
        }

        user.login();
        return new AuthenticationResult(true, "Login successful", user);
    }

    // ----------------------------------------------------------
    // FIXED: Employee ID now OPTIONAL → default null if not given
    // ----------------------------------------------------------
    public UserCreationResult createUser(String username, String password, UserRole role) {
        return createUser(username, password, role, null);
    }

    // Full version (admin assigns empid)
    public UserCreationResult createUser(String username, String password, UserRole role, Integer empid) {

        if (username == null || username.isBlank() ||
            password == null || password.isBlank() ||
            role == null) {
            return new UserCreationResult(false, "All fields are required.");
        }

        String normalized = username.trim().toLowerCase();

        if (userDAO.findByUsername(normalized) != null) {
            return new UserCreationResult(false, "Username already exists.");
        }

        // basic password strength check
        var pwCheck = SecurityUtils.validatePasswordStrength(password);
        if (!pwCheck.isValid()) {
            return new UserCreationResult(false, "Weak password: " + pwCheck.getErrors());
        }

        String hash = SecurityUtils.hashPassword(password);

        User newUser = new User(normalized, hash, role, empid);

        boolean saved = userDAO.save(newUser);
        if (!saved) {
            return new UserCreationResult(false, "Database error: could not save user.");
        }

        return new UserCreationResult(true, "Account created successfully!");
    }

    public void logout() {
        System.out.println("[AUTH] Logout called");
    }
}
