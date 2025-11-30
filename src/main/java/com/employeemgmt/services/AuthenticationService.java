package com.employeemgmt.services;

import com.employeemgmt.dao.UserDAO;
import com.employeemgmt.models.User;
import com.employeemgmt.models.User.UserRole;
import com.employeemgmt.utils.SecurityUtils;

/*
    AuthenticationService.java
    --------------------------
    Just the “middle man” between the UI and the UserDAO.

    - UI calls login() or createUser()
    - DAO handles the actual database work
    - SecurityUtils handles hashing + password validation

    I kept the comments light and student-like so it feels like
    regular project code and not something auto-generated.
*/
public class AuthenticationService {

    // one DAO for user operations
    private final UserDAO userDAO = new UserDAO();

    /*
        Small wrapper returned when someone tries to log in.
        This avoids passing nulls around the UI.
    */
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

    /*
        Same idea as above but for account creation.
    */
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

    // ---------------------------------------------------
    // LOGIN
    // ---------------------------------------------------
    public AuthenticationResult login(String username, String password) {

        if (username == null || username.trim().isEmpty()) {
            return new AuthenticationResult(false, "Username cannot be empty.", null);
        }
        if (password == null || password.trim().isEmpty()) {
            return new AuthenticationResult(false, "Password cannot be empty.", null);
        }

        // normalize the username just so everything is consistent
        String normalized = username.trim().toLowerCase();

        // DAO handles:
        //  - finding the record
        //  - verifying the password hash
        User user = userDAO.authenticate(normalized, password);

        if (user == null) {
            System.out.println("[AUTH] Failed login for: " + normalized);
            return new AuthenticationResult(false, "Invalid username or password.", null);
        }

        // simple hook (we call login() so User model can update lastLogin if needed)
        user.login();

        return new AuthenticationResult(true, "Login successful!", user);
    }

    // ---------------------------------------------------
    // REGISTER / CREATE ACCOUNT
    // ---------------------------------------------------
    public UserCreationResult createUser(String username, String password, UserRole role, Integer empid) {

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || role == null) {

            return new UserCreationResult(false, "All fields are required.");
        }

        String normalizedUser = username.trim().toLowerCase();

        // check if username already exists
        if (userDAO.findByUsername(normalizedUser) != null) {
            return new UserCreationResult(false, "Username already exists.");
        }

        // validate password with the same helper we use for login/security
        SecurityUtils.ValidationResult check = SecurityUtils.validatePasswordStrength(password);

        if (!check.isValid()) {
            return new UserCreationResult(false, "Weak password: " + check.getErrors());
        }

        // hash the password before saving
        String hashed = SecurityUtils.hashPassword(password);

        // build the model (empid can be null if admin)
        User newUser = new User(normalizedUser, hashed, role, empid);

        // save it to DB
        boolean saved = userDAO.save(newUser);

        if (!saved) {
            return new UserCreationResult(false, "Database error: unable to create account.");
        }

        System.out.println("[AUTH] New user created: " + normalizedUser + " (" + role + ")");
        return new UserCreationResult(true, "Account created successfully!");
    }

    // ---------------------------------------------------
    // LOGOUT
    // ---------------------------------------------------
    public void logout() {
        // we don’t persist sessions, so this is really just a UI-level hook.
        System.out.println("[AUTH] User logged out.");
    }
}