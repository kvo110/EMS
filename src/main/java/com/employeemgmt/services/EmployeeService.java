package com.employeemgmt.services;

import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.dao.EmployeeDAO.SearchCriteria;
import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * Employee Service Layer
 * Handles business logic for employee operations with role-based access control
 */
public class EmployeeService {
    
    private EmployeeDAO employeeDAO;
    
    // Business rule constants
    private static final BigDecimal MIN_SALARY = new BigDecimal("30000.00");
    private static final BigDecimal MAX_SALARY = new BigDecimal("500000.00");
    private static final int MIN_AGE = 16;
    private static final int MAX_AGE = 100;
    
    // Constructor
    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }
    
    /**
     * Search employees with role-based access control
     * @param searchCriteria The search criteria
     * @param currentUser The current logged-in user
     * @return SearchResult containing employees and access information
     */
    public SearchResult searchEmployees(SearchCriteria searchCriteria, User currentUser) {
        SearchResult result = new SearchResult();
        
        // Validate user authentication
        if (currentUser == null || !currentUser.isLoggedIn()) {
            result.setSuccess(false);
            result.setMessage("User must be logged in to search employees");
            return result;
        }
        
        // Role-based access control
        if (currentUser.isAdmin()) {
            // HR Admin: Can search all employees
            return performAdminSearch(searchCriteria, result);
        } else if (currentUser.isEmployee() && currentUser.hasEmployeeRecord()) {
            // General Employee: Can only view their own data
            return performEmployeeSearch(currentUser.getEmpid(), result);
        } else {
            result.setSuccess(false);
            result.setMessage("Insufficient permissions to search employees");
            return result;
        }
    }
    
    /**
     * Search employee by ID with role-based access
     * @param empid The employee ID to search for
     * @param currentUser The current logged-in user
     * @return SearchResult containing the employee if authorized
     */
    public SearchResult searchEmployeeById(int empid, User currentUser) {
        SearchResult result = new SearchResult();
        
        // Validate user authentication
        if (currentUser == null || !currentUser.isLoggedIn()) {
            result.setSuccess(false);
            result.setMessage("User must be logged in to search employees");
            return result;
        }
        
        // Role-based access control
        if (currentUser.isAdmin()) {
            // HR Admin: Can search any employee
            Optional<Employee> employee = employeeDAO.findById(empid);
            if (employee.isPresent()) {
                List<Employee> employees = new ArrayList<>();
                employees.add(employee.get());
                result.setSuccess(true);
                result.setEmployees(employees);
                result.setMessage("Employee found");
            } else {
                result.setSuccess(false);
                result.setMessage("Employee not found");
            }
        } else if (currentUser.isEmployee() && currentUser.hasEmployeeRecord()) {
            // General Employee: Can only view their own data
            if (empid == currentUser.getEmpid()) {
                Optional<Employee> employee = employeeDAO.findById(empid);
                if (employee.isPresent()) {
                    List<Employee> employees = new ArrayList<>();
                    employees.add(employee.get());
                    result.setSuccess(true);
                    result.setEmployees(employees);
                    result.setMessage("Your employee record");
                } else {
                    result.setSuccess(false);
                    result.setMessage("Your employee record not found");
                }
            } else {
                result.setSuccess(false);
                result.setMessage("You can only view your own employee data");
            }
        } else {
            result.setSuccess(false);
            result.setMessage("Insufficient permissions to search employees");
        }
        
        return result;
    }
    
    /**
     * Quick search by name (for HR Admin only)
     * @param firstName First name (partial matching supported)
     * @param lastName Last name (partial matching supported)
     * @param currentUser The current logged-in user
     * @return SearchResult containing matching employees
     */
    public SearchResult searchByName(String firstName, String lastName, User currentUser) {
        SearchResult result = new SearchResult();
        
        // Validate user authentication and authorization
        if (currentUser == null || !currentUser.isLoggedIn()) {
            result.setSuccess(false);
            result.setMessage("User must be logged in to search employees");
            return result;
        }
        
        if (!currentUser.isAdmin()) {
            result.setSuccess(false);
            result.setMessage("Only HR administrators can search employees by name");
            return result;
        }
        
        // Validate input
        if ((firstName == null || firstName.trim().isEmpty()) && 
            (lastName == null || lastName.trim().isEmpty())) {
            result.setSuccess(false);
            result.setMessage("Please provide at least first name or last name");
            return result;
        }
        
        // Perform search
        List<Employee> employees = employeeDAO.findByName(
            firstName != null ? firstName.trim() : "", 
            lastName != null ? lastName.trim() : ""
        );
        
        result.setSuccess(true);
        result.setEmployees(employees);
        result.setMessage("Found " + employees.size() + " employee(s)");
        
        return result;
    }
    
    /**
     * Search by SSN (for HR Admin only)
     * @param ssn The Social Security Number
     * @param currentUser The current logged-in user
     * @return SearchResult containing the employee if found
     */
    public SearchResult searchBySSN(String ssn, User currentUser) {
        SearchResult result = new SearchResult();
        
        // Validate user authentication and authorization
        if (currentUser == null || !currentUser.isLoggedIn()) {
            result.setSuccess(false);
            result.setMessage("User must be logged in to search employees");
            return result;
        }
        
        if (!currentUser.isAdmin()) {
            result.setSuccess(false);
            result.setMessage("Only HR administrators can search employees by SSN");
            return result;
        }
        
        // Validate SSN format
        if (ssn == null || !ssn.matches("\\d{3}-\\d{2}-\\d{4}")) {
            result.setSuccess(false);
            result.setMessage("Invalid SSN format. Please use XXX-XX-XXXX format");
            return result;
        }
        
        // Perform search
        Optional<Employee> employee = employeeDAO.findBySSN(ssn);
        
        if (employee.isPresent()) {
            List<Employee> employees = new ArrayList<>();
            employees.add(employee.get());
            result.setSuccess(true);
            result.setEmployees(employees);
            result.setMessage("Employee found");
        } else {
            result.setSuccess(false);
            result.setMessage("No employee found with SSN: " + ssn);
        }
        
        return result;
    }
    
    /**
     * Search by date of birth (for HR Admin only)
     * @param dob The date of birth
     * @param currentUser The current logged-in user
     * @return SearchResult containing matching employees
     */
    public SearchResult searchByDOB(LocalDate dob, User currentUser) {
        SearchResult result = new SearchResult();
        
        // Validate user authentication and authorization
        if (currentUser == null || !currentUser.isLoggedIn()) {
            result.setSuccess(false);
            result.setMessage("User must be logged in to search employees");
            return result;
        }
        
        if (!currentUser.isAdmin()) {
            result.setSuccess(false);
            result.setMessage("Only HR administrators can search employees by date of birth");
            return result;
        }
        
        // Validate date
        if (dob == null) {
            result.setSuccess(false);
            result.setMessage("Please provide a valid date of birth");
            return result;
        }
        
        // Perform search
        List<Employee> employees = employeeDAO.findByDOB(dob);
        
        result.setSuccess(true);
        result.setEmployees(employees);
        result.setMessage("Found " + employees.size() + " employee(s) with DOB: " + dob);
        
        return result;
    }
    
    /**
     * Get all employees (for HR Admin only)
     * @param currentUser The current logged-in user
     * @return SearchResult containing all employees
     */
    public SearchResult getAllEmployees(User currentUser) {
        SearchResult result = new SearchResult();
        
        // Validate user authentication and authorization
        if (currentUser == null || !currentUser.isLoggedIn()) {
            result.setSuccess(false);
            result.setMessage("User must be logged in to view employees");
            return result;
        }
        
        if (!currentUser.isAdmin()) {
            result.setSuccess(false);
            result.setMessage("Only HR administrators can view all employees");
            return result;
        }
        
        // Get all employees
        List<Employee> employees = employeeDAO.findAll();
        
        result.setSuccess(true);
        result.setEmployees(employees);
        result.setMessage("Retrieved " + employees.size() + " employee(s)");
        
        return result;
    }
    
    /**
     * Validate employee data for business rules
     * @param employee The employee to validate
     * @return ValidationResult with success status and messages
     */
    public ValidationResult validateEmployeeData(Employee employee) {
        ValidationResult result = new ValidationResult();
        
        if (employee == null) {
            result.addError("Employee cannot be null");
            return result;
        }
        
        // Validate basic employee data
        if (!employee.isValid()) {
            result.addError("Employee data is incomplete or invalid");
        }
        
        // Validate salary range
        if (employee.getBaseSalary() != null) {
            if (employee.getBaseSalary().compareTo(MIN_SALARY) < 0) {
                result.addError("Salary cannot be less than " + MIN_SALARY);
            }
            if (employee.getBaseSalary().compareTo(MAX_SALARY) > 0) {
                result.addError("Salary cannot exceed " + MAX_SALARY);
            }
        }
        
        // Validate age
        if (employee.getDob() != null) {
            int age = employee.getAge();
            if (age < MIN_AGE) {
                result.addError("Employee must be at least " + MIN_AGE + " years old");
            }
            if (age > MAX_AGE) {
                result.addError("Employee age cannot exceed " + MAX_AGE + " years");
            }
        }
        
        // Validate hire date
        if (employee.getHireDate() != null && employee.getHireDate().isAfter(LocalDate.now())) {
            result.addError("Hire date cannot be in the future");
        }
        
        // Validate email format
        if (employee.getEmail() != null && !employee.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            result.addError("Invalid email format");
        }
        
        return result;
    }
    
    /**
     * Perform admin search (unrestricted)
     */
    private SearchResult performAdminSearch(SearchCriteria searchCriteria, SearchResult result) {
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            // Return all employees if no criteria provided
            List<Employee> employees = employeeDAO.findAll();
            result.setSuccess(true);
            result.setEmployees(employees);
            result.setMessage("Retrieved " + employees.size() + " employee(s)");
        } else {
            // Perform filtered search
            List<Employee> employees = employeeDAO.searchEmployees(searchCriteria);
            result.setSuccess(true);
            result.setEmployees(employees);
            result.setMessage("Found " + employees.size() + " matching employee(s)");
        }
        return result;
    }
    
    /**
     * Perform employee search (restricted to own data)
     */
    private SearchResult performEmployeeSearch(int empid, SearchResult result) {
        Optional<Employee> employee = employeeDAO.findById(empid);
        
        if (employee.isPresent()) {
            List<Employee> employees = new ArrayList<>();
            employees.add(employee.get());
            result.setSuccess(true);
            result.setEmployees(employees);
            result.setMessage("Your employee record");
        } else {
            result.setSuccess(false);
            result.setMessage("Your employee record not found");
        }
        
        return result;
    }
    
    /**
     * Result class for search operations
     */
    public static class SearchResult {
        private boolean success;
        private String message;
        private List<Employee> employees;
        
        public SearchResult() {
            this.employees = new ArrayList<>();
        }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public List<Employee> getEmployees() { return employees; }
        public void setEmployees(List<Employee> employees) { 
            this.employees = employees != null ? employees : new ArrayList<>(); 
        }
        
        public int getCount() { return employees.size(); }
    }
    
    /**
     * Result class for validation operations
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
        
        public boolean isValid() { return isValid; }
        
        public String getErrors() { return errors.toString(); }
    }
}