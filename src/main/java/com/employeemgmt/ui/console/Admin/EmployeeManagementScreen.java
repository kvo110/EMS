package com.employeemgmt.ui.console.Admin;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;

import java.util.List;
import java.util.Scanner;

/*
   EmployeeManagementScreen
   ------------------------
   Basic menu for HR admins to look up employees.
   This is intentionally simple so it doesn't fight
   with the GUI requirements.
*/
public class EmployeeManagementScreen {

    private final Scanner scanner = new Scanner(System.in);
    private final EmployeeService employeeService = new EmployeeService();

    public void show(User admin) {
        if (admin == null || !admin.isAdmin()) {
            System.out.println("Only admins can manage employees.");
            return;
        }

        boolean stay = true;

        while (stay) {
            System.out.println("\n--- Employee Management (Console) ---");
            System.out.println("1. View all employees");
            System.out.println("2. Search by ID");
            System.out.println("3. Search by Name");
            System.out.println("4. Back to Admin Dashboard");
            System.out.print("Pick an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showAllEmployees(admin);
                case "2" -> searchById(admin);
                case "3" -> searchByName(admin);
                case "4" -> stay = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void showAllEmployees(User admin) {
        var result = employeeService.getAllEmployees(admin);
        if (!result.isSuccess()) {
            System.out.println("Error: " + result.getMessage());
            return;
        }

        List<Employee> employees = result.getEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.println("\nID   Name                     Email");
        System.out.println("------------------------------------------");
        for (Employee e : employees) {
            System.out.printf("%-4d %-22s %-25s%n",
                    e.getEmpid(),
                    e.getFullName(),
                    e.getEmail());
        }
    }

    private void searchById(User admin) {
        System.out.print("Enter Employee ID: ");
        String input = scanner.nextLine().trim();
        try {
            int id = Integer.parseInt(input);
            var result = employeeService.searchEmployeeById(id, admin);

            if (!result.isSuccess() || result.getEmployees().isEmpty()) {
                System.out.println("Not found: " + result.getMessage());
                return;
            }

            Employee e = result.getEmployees().get(0);
            System.out.println("Found: " + e.getEmpid() + " - " + e.getFullName() + " (" + e.getEmail() + ")");
        } catch (NumberFormatException ex) {
            System.out.println("Invalid ID.");
        }
    }

    private void searchByName(User admin) {
        System.out.print("First name (or blank): ");
        String first = scanner.nextLine().trim();
        System.out.print("Last name (or blank): ");
        String last = scanner.nextLine().trim();

        var result = employeeService.searchByName(first, last, admin);

        if (!result.isSuccess()) {
            System.out.println("Error: " + result.getMessage());
            return;
        }

        List<Employee> employees = result.getEmployees();
        if (employees.isEmpty()) {
            System.out.println("No matching employees.");
            return;
        }

        System.out.println("\nMatches:");
        for (Employee e : employees) {
            System.out.println("- " + e.getEmpid() + " | " + e.getFullName() + " | " + e.getEmail());
        }
    }
}