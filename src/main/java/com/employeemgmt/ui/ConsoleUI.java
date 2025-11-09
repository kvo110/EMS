package com.employeemgmt.ui;

import com.employeemgmt.models.*;
import com.employeemgmt.services.*;
import com.employeemgmt.dao.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced Console User Interface
 * Professional text-based interface for Employee Management System
 */
public class ConsoleUI {
    
    private final Scanner scanner;
    private final AuthenticationService authService;
    private final EmployeeService employeeService;
    private final ReportService reportService;
    private User currentUser;
    private boolean running;
    
    // Display constants
    private static final String HEADER_LINE = "=".repeat(60);
    private static final String SUB_HEADER_LINE = "-".repeat(40);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthenticationService();
        this.employeeService = new EmployeeService();
        this.reportService = new ReportService();
        this.running = true;
    }
    
    /**
     * Main application entry point
     */
    public void start() {
        displayWelcome();
        
        while (running) {
            if (currentUser == null) {
                if (!handleLogin()) {
                    break;
                }
            }
            
            try {
                if (currentUser.isAdmin()) {
                    showAdminMenu();
                } else {
                    showEmployeeMenu();
                }
            } catch (Exception e) {
                displayError("System error: " + e.getMessage());
                pauseForUser();
            }
        }
        
        displayGoodbye();
        scanner.close();
    }
    
    /**
     * Display welcome screen
     */
    private void displayWelcome() {
        clearScreen();
        System.out.println(HEADER_LINE);
        System.out.println("           EMPLOYEE MANAGEMENT SYSTEM");
        System.out.println("                  Version 1.0");
        System.out.println(HEADER_LINE);
        System.out.println();
    }
    
    /**
     * Handle user login with validation
     */
    private boolean handleLogin() {
        System.out.println("\n" + SUB_HEADER_LINE);
        System.out.println("              LOGIN REQUIRED");
        System.out.println(SUB_HEADER_LINE);
        
        int attempts = 0;
        final int maxAttempts = 3;
        
        while (attempts < maxAttempts) {
            System.out.print("\nUsername: ");
            String username = scanner.nextLine().trim();
            
            if (username.isEmpty()) {
                displayError("Username cannot be empty.");
                continue;
            }
            
            System.out.print("Password: ");
            String password = scanner.nextLine();
            
            if (password.isEmpty()) {
                displayError("Password cannot be empty.");
                continue;
            }
            
            // Attempt authentication
            AuthenticationService.AuthenticationResult result = authService.login(username, password);
            
            if (result.isSuccess()) {
                currentUser = result.getUser();
                displaySuccess("Login successful! Welcome, " + 
                    (currentUser.isAdmin() ? "HR Administrator" : "Employee") + "!");
                pauseForUser();
                return true;
            } else {
                attempts++;
                displayError("Login failed: " + result.getMessage());
                
                if (attempts < maxAttempts) {
                    System.out.println("Attempts remaining: " + (maxAttempts - attempts));
                } else {
                    displayError("Maximum login attempts exceeded. Exiting...");
                    return false;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Show HR Administrator menu
     */
    private void showAdminMenu() {
        while (running && currentUser != null) {
            clearScreen();
            displayHeader("HR ADMINISTRATOR DASHBOARD");
            System.out.println("Welcome, " + currentUser.getUsername());
            System.out.println();
            
            System.out.println("1. Employee Management");
            System.out.println("2. Salary Management");
            System.out.println("3. Reports");
            System.out.println("4. System Administration");
            System.out.println("5. Logout");
            System.out.println("6. Exit Application");
            
            int choice = getMenuChoice(1, 6);
            
            switch (choice) {
                case 1 -> showEmployeeManagementMenu();
                case 2 -> showSalaryManagementMenu();
                case 3 -> showReportsMenu();
                case 4 -> showSystemAdminMenu();
                case 5 -> logout();
                case 6 -> exitApplication();
            }
        }
    }
    
    /**
     * Show Employee menu
     */
    private void showEmployeeMenu() {
        while (running && currentUser != null) {
            clearScreen();
            displayHeader("EMPLOYEE DASHBOARD");
            System.out.println("Welcome, " + currentUser.getUsername());
            System.out.println();
            
            System.out.println("1. View My Profile");
            System.out.println("2. View My Pay History");
            System.out.println("3. Update My Contact Information");
            System.out.println("4. Logout");
            System.out.println("5. Exit Application");
            
            int choice = getMenuChoice(1, 5);
            
            switch (choice) {
                case 1 -> viewMyProfile();
                case 2 -> viewMyPayHistory();
                case 3 -> updateMyContactInfo();
                case 4 -> logout();
                case 5 -> exitApplication();
            }
        }
    }
    
    /**
     * Employee Management submenu
     */
    private void showEmployeeManagementMenu() {
        while (true) {
            clearScreen();
            displayHeader("EMPLOYEE MANAGEMENT");
            
            System.out.println("1. Search Employees");
            System.out.println("2. View All Employees");
            System.out.println("3. Add New Employee");
            System.out.println("4. Update Employee");
            System.out.println("5. Employee Details");
            System.out.println("6. Back to Main Menu");
            
            int choice = getMenuChoice(1, 6);
            
            switch (choice) {
                case 1 -> searchEmployees();
                case 2 -> viewAllEmployees();
                case 3 -> addNewEmployee();
                case 4 -> updateEmployee();
                case 5 -> viewEmployeeDetails();
                case 6 -> { return; }
            }
        }
    }
    
    /**
     * Salary Management submenu
     */
    private void showSalaryManagementMenu() {
        while (true) {
            clearScreen();
            displayHeader("SALARY MANAGEMENT");
            
            System.out.println("1. Update Salaries by Range");
            System.out.println("2. Update Individual Salary");
            System.out.println("3. Salary Analysis Report");
            System.out.println("4. Back to Main Menu");
            
            int choice = getMenuChoice(1, 4);
            
            switch (choice) {
                case 1 -> updateSalariesByRange();
                case 2 -> updateIndividualSalary();
                case 3 -> showSalaryAnalysis();
                case 4 -> { return; }
            }
        }
    }
    
    /**
     * Reports submenu
     */
    private void showReportsMenu() {
        while (true) {
            clearScreen();
            displayHeader("REPORTS");
            
            System.out.println("1. Pay Statements by Employee");
            System.out.println("2. Total Pay by Job Title");
            System.out.println("3. Total Pay by Division");
            System.out.println("4. Employees Hired in Date Range");
            System.out.println("5. Custom Employee Report");
            System.out.println("6. Back to Main Menu");
            
            int choice = getMenuChoice(1, 6);
            
            switch (choice) {
                case 1 -> showPayStatements();
                case 2 -> showPayByJobTitle();
                case 3 -> showPayByDivision();
                case 4 -> showEmployeesHiredInRange();
                case 5 -> showCustomReport();
                case 6 -> { return; }
            }
        }
    }
    
    /**
     * Search employees with multiple criteria
     */
    private void searchEmployees() {
        clearScreen();
        displayHeader("SEARCH EMPLOYEES");
        
        System.out.println("Search by:");
        System.out.println("1. Employee ID");
        System.out.println("2. Name (First/Last)");
        System.out.println("3. SSN");
        System.out.println("4. Date of Birth");
        System.out.println("5. Advanced Search");
        System.out.println("6. Back");
        
        int choice = getMenuChoice(1, 6);
        
        switch (choice) {
            case 1 -> searchByEmployeeId();
            case 2 -> searchByName();
            case 3 -> searchBySSN();
            case 4 -> searchByDateOfBirth();
            case 5 -> advancedSearch();
            case 6 -> { return; }
        }
    }
    
    /**
     * Search by Employee ID
     */
    private void searchByEmployeeId() {
        System.out.print("\nEnter Employee ID: ");
        try {
            int empId = Integer.parseInt(scanner.nextLine().trim());
            
            EmployeeService.SearchResult result = employeeService.searchEmployeeById(empId, currentUser);
            
            if (result.isSuccess() && !result.getEmployees().isEmpty()) {
                displayEmployeeList(result.getEmployees(), "Employee Found");
            } else {
                displayError("Employee not found with ID: " + empId);
            }
        } catch (NumberFormatException e) {
            displayError("Invalid Employee ID format.");
        }
        
        pauseForUser();
    }
    
    /**
     * Search by name
     */
    private void searchByName() {
        System.out.print("\nEnter first name (or press Enter to skip): ");
        String firstName = scanner.nextLine().trim();
        
        System.out.print("Enter last name (or press Enter to skip): ");
        String lastName = scanner.nextLine().trim();
        
        if (firstName.isEmpty() && lastName.isEmpty()) {
            displayError("Please provide at least first name or last name.");
            pauseForUser();
            return;
        }
        
        EmployeeService.SearchResult result = employeeService.searchByName(firstName, lastName, currentUser);
        
        if (result.isSuccess()) {
            if (result.getEmployees().isEmpty()) {
                displayInfo("No employees found matching the name criteria.");
            } else {
                displayEmployeeList(result.getEmployees(), "Search Results");
            }
        } else {
            displayError("Search failed: " + result.getMessage());
        }
        
        pauseForUser();
    }
    
    /**
     * View all employees
     */
    private void viewAllEmployees() {
        clearScreen();
        displayHeader("ALL EMPLOYEES");
        
        EmployeeService.SearchResult result = employeeService.getAllEmployees(currentUser);
        
        if (result.isSuccess()) {
            if (result.getEmployees().isEmpty()) {
                displayInfo("No employees found in the system.");
            } else {
                displayEmployeeList(result.getEmployees(), "All Employees (" + result.getCount() + " total)");
            }
        } else {
            displayError("Failed to retrieve employees: " + result.getMessage());
        }
        
        pauseForUser();
    }
    
    /**
     * Display formatted employee list
     */
    private void displayEmployeeList(List<Employee> employees, String title) {
        System.out.println("\n" + SUB_HEADER_LINE);
        System.out.println(title);
        System.out.println(SUB_HEADER_LINE);
        
        System.out.printf("%-5s %-20s %-30s %-12s %-15s%n", 
            "ID", "Name", "Email", "Hire Date", "Salary");
        System.out.println("-".repeat(85));
        
        for (Employee emp : employees) {
            System.out.printf("%-5d %-20s %-30s %-12s %-15s%n",
                emp.getEmpid(),
                truncate(emp.getFullName(), 20),
                truncate(emp.getEmail(), 30),
                emp.getHireDate() != null ? emp.getHireDate().format(DATE_FORMAT) : "N/A",
                emp.getFormattedSalary());
        }
        
        System.out.println("-".repeat(85));
        System.out.println("Total: " + employees.size() + " employee(s)");
    }
    
    /**
     * View my profile (Employee)
     */
    private void viewMyProfile() {
        clearScreen();
        displayHeader("MY PROFILE");
        
        if (currentUser.getEmpid() <= 0) {
            displayError("Employee ID not found. Please contact HR.");
            pauseForUser();
            return;
        }
        
        EmployeeService.SearchResult result = employeeService.searchEmployeeById(currentUser.getEmpid(), currentUser);
        
        if (result.isSuccess() && !result.getEmployees().isEmpty()) {
            Employee emp = result.getEmployees().get(0);
            displayEmployeeDetails(emp);
        } else {
            displayError("Could not retrieve your profile information.");
        }
        
        pauseForUser();
    }
    
    /**
     * Display detailed employee information
     */
    private void displayEmployeeDetails(Employee emp) {
        System.out.println();
        System.out.println("Employee ID: " + emp.getEmpid());
        System.out.println("Name: " + emp.getFullName());
        System.out.println("Email: " + emp.getEmail());
        System.out.println("SSN: " + (emp.getSsn() != null ? maskSSN(emp.getSsn()) : "N/A"));
        System.out.println("Date of Birth: " + (emp.getDob() != null ? emp.getDob().format(DATE_FORMAT) : "N/A"));
        System.out.println("Hire Date: " + (emp.getHireDate() != null ? emp.getHireDate().format(DATE_FORMAT) : "N/A"));
        System.out.println("Base Salary: " + emp.getFormattedSalary());
        System.out.println("Age: " + emp.getAge() + " years");
        System.out.println("Years of Service: " + emp.getYearsOfService() + " years");
        System.out.println("Status: " + (emp.isActive() ? "Active" : "Inactive"));
    }
    
    /**
     * Update salaries by range
     */
    private void updateSalariesByRange() {
        clearScreen();
        displayHeader("UPDATE SALARIES BY RANGE");
        
        try {
            System.out.print("\nEnter minimum salary range: $");
            BigDecimal minSalary = new BigDecimal(scanner.nextLine().trim());
            
            System.out.print("Enter maximum salary range: $");
            BigDecimal maxSalary = new BigDecimal(scanner.nextLine().trim());
            
            System.out.print("Enter percentage increase (e.g., 3.5 for 3.5%): ");
            double percentage = Double.parseDouble(scanner.nextLine().trim());
            
            if (percentage <= 0 || percentage > 50) {
                displayError("Percentage must be between 0.1 and 50.");
                pauseForUser();
                return;
            }
            
            // Confirm the operation
            System.out.println("\nSummary:");
            System.out.println("Salary Range: $" + minSalary + " - $" + maxSalary);
            System.out.println("Increase: " + percentage + "%");
            System.out.print("\nConfirm this salary update? (y/N): ");
            
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (!confirm.equals("y") && !confirm.equals("yes")) {
                displayInfo("Salary update cancelled.");
                pauseForUser();
                return;
            }
            
            // Perform the update (this would integrate with your EmployeeDAO)
            displaySuccess("Salary update completed successfully!");
            displayInfo("Note: This is a simulation. Integration with EmployeeDAO.updateSalariesByRange() needed.");
            
        } catch (NumberFormatException e) {
            displayError("Invalid number format. Please enter valid numbers.");
        } catch (Exception e) {
            displayError("Error updating salaries: " + e.getMessage());
        }
        
        pauseForUser();
    }
    
    /**
     * Show pay statements report
     */
    private void showPayStatements() {
        clearScreen();
        displayHeader("PAY STATEMENTS");
        
        if (!currentUser.isAdmin()) {
            // Employee can only see their own pay statements
            displayInfo("Showing your pay statement history...");
            // Integration with PayrollDAO needed
            displayInfo("Note: Integration with PayrollDAO.getPayStatementHistory() needed.");
        } else {
            System.out.print("\nEnter Employee ID (or press Enter for all): ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                displayInfo("Showing pay statements for all employees...");
            } else {
                try {
                    int empId = Integer.parseInt(input);
                    displayInfo("Showing pay statements for Employee ID: " + empId);
                } catch (NumberFormatException e) {
                    displayError("Invalid Employee ID format.");
                    pauseForUser();
                    return;
                }
            }
            
            // Integration with PayrollDAO needed
            displayInfo("Note: Integration with PayrollDAO.getPayStatementHistory() needed.");
        }
        
        pauseForUser();
    }
    
    /**
     * Utility methods
     */
    private void clearScreen() {
        // Simple clear screen simulation
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    
    private void displayHeader(String title) {
        System.out.println(HEADER_LINE);
        System.out.println(centerText(title, 60));
        System.out.println(HEADER_LINE);
    }
    
    private void displaySuccess(String message) {
        System.out.println("\n✓ SUCCESS: " + message);
    }
    
    private void displayError(String message) {
        System.out.println("\n✗ ERROR: " + message);
    }
    
    private void displayInfo(String message) {
        System.out.println("\nℹ INFO: " + message);
    }
    
    private void pauseForUser() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private int getMenuChoice(int min, int max) {
        while (true) {
            System.out.print("\nEnter your choice (" + min + "-" + max + "): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    displayError("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                displayError("Please enter a valid number.");
            }
        }
    }
    
    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }
    
    private String truncate(String text, int maxLength) {
        if (text == null) return "N/A";
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }
    
    private String maskSSN(String ssn) {
        if (ssn == null || ssn.length() < 4) return "***-**-****";
        return "***-**-" + ssn.substring(ssn.length() - 4);
    }
    
    private void logout() {
        authService.logout();
        currentUser = null;
        displayInfo("You have been logged out successfully.");
        pauseForUser();
    }
    
    private void exitApplication() {
        System.out.print("\nAre you sure you want to exit? (y/N): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            running = false;
        }
    }
    
    private void displayGoodbye() {
        clearScreen();
        System.out.println(HEADER_LINE);
        System.out.println(centerText("Thank you for using EMS!", 60));
        System.out.println(centerText("Goodbye!", 60));
        System.out.println(HEADER_LINE);
    }
    
    // Placeholder methods for remaining functionality
    private void showSystemAdminMenu() {
        displayInfo("System Administration features coming soon...");
        pauseForUser();
    }
    
    private void viewMyPayHistory() {
        displayInfo("Pay history integration with PayrollDAO needed.");
        pauseForUser();
    }
    
    private void updateMyContactInfo() {
        displayInfo("Contact info update feature coming soon...");
        pauseForUser();
    }
    
    private void addNewEmployee() {
        displayInfo("Add employee integration with EmployeeDAO needed.");
        pauseForUser();
    }
    
    private void updateEmployee() {
        displayInfo("Update employee integration with EmployeeDAO needed.");
        pauseForUser();
    }
    
    private void viewEmployeeDetails() {
        displayInfo("Employee details view coming soon...");
        pauseForUser();
    }
    
    private void searchBySSN() {
        displayInfo("SSN search integration needed.");
        pauseForUser();
    }
    
    private void searchByDateOfBirth() {
        displayInfo("DOB search integration needed.");
        pauseForUser();
    }
    
    private void advancedSearch() {
        displayInfo("Advanced search coming soon...");
        pauseForUser();
    }
    
    private void updateIndividualSalary() {
        displayInfo("Individual salary update coming soon...");
        pauseForUser();
    }
    
    private void showSalaryAnalysis() {
        displayInfo("Salary analysis report coming soon...");
        pauseForUser();
    }
    
    private void showPayByJobTitle() {
        displayInfo("Pay by job title report integration needed.");
        pauseForUser();
    }
    
    private void showPayByDivision() {
        displayInfo("Pay by division report integration needed.");
        pauseForUser();
    }
    
    private void showEmployeesHiredInRange() {
        displayInfo("Employees hired in range report integration needed.");
        pauseForUser();
    }
    
    private void showCustomReport() {
        displayInfo("Custom report builder coming soon...");
        pauseForUser();
    }
}
