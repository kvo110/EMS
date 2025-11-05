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
import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User.UserRole;
import com.employeemgmt.services.EmployeeService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
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
        UserRole role;
        if (username.equals("admin") && password.equals("admin123")) {
            role = UserRole.HR_ADMIN;
            System.out.println("Login successful. Welcome HR Admin!");
        } else if (username.equals("employee") && password.equals("emp123")) {
            role = UserRole.GENERAL_EMPLOYEE;
            System.out.println("Login successful. Welcome Employee!");
        } else {
            System.out.println("Invalid credentials. Exiting...");
            scanner.close();
            return;
        }

        try {
            // Initialize database connection
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/employeeData", "root", "your_password");

            EmployeeDAO dao = new EmployeeDAO(conn);
            EmployeeService service = new EmployeeService(conn, dao);

            // Route based on role
            if (role == UserRole.HR_ADMIN) {
                System.out.println("1) Add new employee");
                System.out.println("2) Batch salary update");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                if (choice == 1) {
                    Employee emp = new Employee();
                    System.out.print("First name: ");
                    emp.setFname(scanner.nextLine());
                    System.out.print("Last name: ");
                    emp.setLname(scanner.nextLine());
                    System.out.print("Email: ");
                    emp.setEmail(scanner.nextLine());
                    emp.setHireDate(Date.valueOf("2023-04-20"));
                    emp.setSalary(BigDecimal.valueOf(60000));
                    emp.setSsn("999-88-7777");

                    service.addNewEmployee(emp, role);
                } else if (choice == 2) {
                    service.updateSalariesByPercentage(3.0, 50000, 100000, role);
                }

            } else if (role == UserRole.GENERAL_EMPLOYEE) {
                System.out.println("Fetching your employee info...");
                var empList = service.searchEmployees("", role, 1);
                empList.forEach(System.out::println);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        scanner.close();
    }
}
