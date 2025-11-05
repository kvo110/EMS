package com.employeemgmt.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Security Utility Class
 * Provides secure password hashing, validation, and token generation
 */
public class SecurityUtils {
    
    // Security constants
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    // Password complexity patterns
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    
    /**
     * Hash a password with salt using SHA-256
     * @param password The plain text password
     * @return The hashed password with salt (format: salt:hash)
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            // Generate salt
            byte[] salt = generateSalt();
            
            // Create hash
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Encode salt and hash to Base64
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Return format: salt:hash
            return saltBase64 + ":" + hashBase64;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     * @param password The plain text password to verify
     * @param storedHash The stored hash (format: salt:hash)
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        
        try {
            // Split stored hash into salt and hash parts
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String expectedHash = parts[1];
            
            // Hash the provided password with the same salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            String actualHash = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Compare hashes
            return expectedHash.equals(actualHash);
            
        } catch (Exception e) {
            // Log error in production, return false for security
            return false;
        }
    }
    
    /**
     * Generate a cryptographically secure salt
     * @return byte array containing random salt
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }
    
    /**
     * Generate a secure random token for sessions
     * @return Base64 encoded secure token
     */
    public static String generateSecureToken() {
        byte[] token = new byte[32]; // 256 bits
        SECURE_RANDOM.nextBytes(token);
        return Base64.getEncoder().encodeToString(token);
    }
    
    /**
     * Validate password strength
     * @param password The password to validate
     * @return ValidationResult with success status and messages
     */
    public static ValidationResult validatePasswordStrength(String password) {
        ValidationResult result = new ValidationResult();
        
        if (password == null || password.isEmpty()) {
            result.addError("Password cannot be empty");
            return result;
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            result.addError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        
        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            result.addError("Password must contain at least one uppercase letter");
        }
        
        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            result.addError("Password must contain at least one lowercase letter");
        }
        
        if (!DIGIT_PATTERN.matcher(password).matches()) {
            result.addError("Password must contain at least one digit");
        }
        
        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            result.addError("Password must contain at least one special character");
        }
        
        return result;
    }
    
    /**
     * Validate username format
     * @param username The username to validate
     * @return ValidationResult with success status and messages
     */
    public static ValidationResult validateUsername(String username) {
        ValidationResult result = new ValidationResult();
        
        if (username == null || username.trim().isEmpty()) {
            result.addError("Username cannot be empty");
            return result;
        }
        
        String trimmed = username.trim();
        
        if (trimmed.length() < 3) {
            result.addError("Username must be at least 3 characters long");
        }
        
        if (trimmed.length() > 50) {
            result.addError("Username cannot exceed 50 characters");
        }
        
        if (!trimmed.matches("^[a-zA-Z0-9._-]+$")) {
            result.addError("Username can only contain letters, numbers, dots, underscores, and hyphens");
        }
        
        return result;
    }
    
    /**
     * Sanitize input to prevent injection attacks
     * @param input The input string to sanitize
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim()
                   .replaceAll("[<>\"'%;()&+]", "") // Remove potentially dangerous characters
                   .replaceAll("\\s+", " "); // Normalize whitespace
    }
    
    /**
     * Check if a string contains SQL injection patterns
     * @param input The input to check
     * @return true if potentially dangerous patterns are found
     */
    public static boolean containsSQLInjectionPatterns(String input) {
        if (input == null) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        String[] dangerousPatterns = {
            "drop table", "delete from", "insert into", "update set",
            "union select", "exec ", "execute ", "sp_", "xp_",
            "script", "javascript", "vbscript", "onload", "onerror"
        };
        
        for (String pattern : dangerousPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Inner class for validation results
     */
    public static class ValidationResult {
        private boolean isValid = true;
        private StringBuilder errors = new StringBuilder();
        
        public void addError(String error) {
            isValid = false;
            if (errors.length() > 0) {
                errors.append("; ");
            }
            errors.append(error);
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getErrors() {
            return errors.toString();
        }
    }
}
