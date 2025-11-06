package com.employeemgmt;

/**
 * Main class for Employee Management System
 * 
 * TODO: Implement main application entry point
 * - Initialize database connection
 * - Launch user interface (Console or JavaFX/Swing GUI)
 * - Handle application lifecycle
 * 
 * Requirements:
 * - Support both HR Admin and General Employee login
 * - Route to appropriate functionality based on user role
 * - Handle graceful shutdown and resource cleanup
 */
import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        // TODO: Implement main method
        // 1. Initialize application
        // 2. Show login screen
        // 3. Route to appropriate interface based on user role

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Employee Management System ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Simple mock login for testing
        User.UserRole role;
        if (username.equals("admin") && password.equals("admin123")) {
            role = User.UserRole.ADMIN;
            System.out.println("Login successful. Welcome HR Admin!");
        } else if (username.equals("employee") && password.equals("emp123")) {
            role = User.UserRole.EMPLOYEE;
            System.out.println("Login successful. Welcome Employee!");
        } else {
            System.out.println("Invalid credentials. Exiting...");
            scanner.close();
            return;
        }

        try {
            // Initialize services using actual implementation
            EmployeeService service = new EmployeeService();

            // Create user for role-based operations
            User currentUser = new User();
            currentUser.setUsername(username);
            currentUser.setRole(role);
            currentUser.login();

            // Route based on role
            if (role == User.UserRole.ADMIN) {
                System.out.println("1) Add new employee");
                System.out.println("2) View all employees");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                if (choice == 1) {
                    Employee emp = new Employee();
                    System.out.print("First name: ");
                    emp.setFirstName(scanner.nextLine());
                    System.out.print("Last name: ");
                    emp.setLastName(scanner.nextLine());
                    System.out.print("Email: ");
                    emp.setEmail(scanner.nextLine());
                    emp.setHireDate(LocalDate.of(2023, 4, 20));
                    emp.setBaseSalary(BigDecimal.valueOf(60000));
                    emp.setSsn("999-88-7777");

                    System.out.println("Employee created: " + emp.getFullName());
                } else if (choice == 2) {
                    EmployeeService.SearchResult result = service.getAllEmployees(currentUser);
                    if (result.isSuccess()) {
                        System.out.println("Found " + result.getCount() + " employees:");
                        result.getEmployees().forEach(emp -> 
                            System.out.println("- " + emp.getFullName() + " (" + emp.getFormattedSalary() + ")"));
                    } else {
                        System.out.println("Error: " + result.getMessage());
                    }
                }

            } else if (role == User.UserRole.EMPLOYEE) {
                System.out.println("Fetching your employee info...");
                currentUser.setEmpid(1); // Mock employee ID
                EmployeeService.SearchResult result = service.searchEmployeeById(1, currentUser);
                if (result.isSuccess() && !result.getEmployees().isEmpty()) {
                    Employee emp = result.getEmployees().get(0);
                    System.out.println("Your info: " + emp.getFullName() + " - " + emp.getFormattedSalary());
                } else {
                    System.out.println("Could not retrieve your information.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        scanner.close();
    }
}
