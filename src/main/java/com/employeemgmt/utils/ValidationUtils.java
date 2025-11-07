package com.employeemgmt.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Validation Utility Class
 * Provides common validation methods for the Employee Management System
 */
public class ValidationUtils {
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    private static final Pattern SSN_PATTERN = Pattern.compile(
        "^\\d{3}-\\d{2}-\\d{4}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+?1[-\\s]?)?(\\(?[0-9]{3}\\)?[-\\s]?[0-9]{3}[-\\s]?[0-9]{4})$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[A-Za-z\\s'-]{1,50}$"
    );
    
    // Salary constraints
    private static final BigDecimal MIN_SALARY = new BigDecimal("30000.00");
    private static final BigDecimal MAX_SALARY = new BigDecimal("500000.00");
    
    // Age constraints
    private static final int MIN_AGE = 16;
    private static final int MAX_AGE = 100;
    
    /**
     * Validate email format
     * @param email The email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate SSN format (XXX-XX-XXXX)
     * @param ssn The SSN to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidSSN(String ssn) {
        if (ssn == null || ssn.trim().isEmpty()) {
            return false;
        }
        return SSN_PATTERN.matcher(ssn.trim()).matches();
    }
    
    /**
     * Validate phone number format
     * @param phone The phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Validate name format (letters, spaces, apostrophes, hyphens only)
     * @param name The name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    /**
     * Validate salary range
     * @param salary The salary to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidSalary(BigDecimal salary) {
        if (salary == null) {
            return false;
        }
        return salary.compareTo(MIN_SALARY) >= 0 && salary.compareTo(MAX_SALARY) <= 0;
    }
    
    /**
     * Validate age range
     * @param age The age to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAge(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }
    
    /**
     * Validate date of birth (not in future, reasonable age)
     * @param dob The date of birth to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDateOfBirth(LocalDate dob) {
        if (dob == null) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        if (dob.isAfter(now)) {
            return false; // Future date
        }
        
        int age = now.getYear() - dob.getYear();
        if (now.getDayOfYear() < dob.getDayOfYear()) {
            age--;
        }
        
        return isValidAge(age);
    }
    
    /**
     * Validate hire date (not in future)
     * @param hireDate The hire date to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidHireDate(LocalDate hireDate) {
        if (hireDate == null) {
            return false;
        }
        return !hireDate.isAfter(LocalDate.now());
    }
    
    /**
     * Parse and validate date string
     * @param dateStr The date string to parse
     * @param formatter The date formatter to use
     * @return LocalDate if valid, null otherwise
     */
    public static LocalDate parseDate(String dateStr, DateTimeFormatter formatter) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateStr.trim(), formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Validate required field
     * @param fieldName The field name for error messages
     * @param value The value to validate
     * @return ValidationResult with success status and message
     */
    public static ValidationResult validateRequired(String fieldName, String value) {
        ValidationResult result = new ValidationResult();
        
        if (value == null || value.trim().isEmpty()) {
            result.setValid(false);
            result.setMessage(fieldName + " is required");
        } else {
            result.setValid(true);
            result.setMessage("Valid");
        }
        
        return result;
    }
    
    /**
     * Sanitize input to prevent XSS and other attacks
     * @param input The input to sanitize
     * @return Sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim()
                   .replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("&", "&amp;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;");
    }
    
    /**
     * Validate employee data comprehensively
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param ssn Social Security Number
     * @param dob Date of birth
     * @param hireDate Hire date
     * @param salary Base salary
     * @return ValidationResult with all validation messages
     */
    public static ValidationResult validateEmployeeData(String firstName, String lastName, 
                                                       String email, String ssn, 
                                                       LocalDate dob, LocalDate hireDate, 
                                                       BigDecimal salary) {
        ValidationResult result = new ValidationResult();
        StringBuilder errors = new StringBuilder();
        
        // Validate required fields
        if (!isValidName(firstName)) {
            errors.append("Invalid first name; ");
        }
        
        if (!isValidName(lastName)) {
            errors.append("Invalid last name; ");
        }
        
        if (!isValidEmail(email)) {
            errors.append("Invalid email format; ");
        }
        
        if (!isValidSSN(ssn)) {
            errors.append("Invalid SSN format (use XXX-XX-XXXX); ");
        }
        
        if (!isValidDateOfBirth(dob)) {
            errors.append("Invalid date of birth; ");
        }
        
        if (!isValidHireDate(hireDate)) {
            errors.append("Invalid hire date; ");
        }
        
        if (!isValidSalary(salary)) {
            errors.append("Invalid salary range ($30,000 - $500,000); ");
        }
        
        if (errors.length() > 0) {
            result.setValid(false);
            result.setMessage(errors.toString().trim());
        } else {
            result.setValid(true);
            result.setMessage("All employee data is valid");
        }
        
        return result;
    }
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid;
        private String message;
        
        public ValidationResult() {
            this.valid = true;
            this.message = "Valid";
        }
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
