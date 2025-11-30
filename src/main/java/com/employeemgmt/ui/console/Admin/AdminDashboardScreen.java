package com.employeemgmt.ui.console.Admin;

import com.employeemgmt.models.User;

import java.util.Scanner;

/*
   AdminDashboardScreen
   --------------------
   Main console menu for HR Admins.
   From here, they can jump to employee management,
   salary tools, reports, etc.
*/
public class AdminDashboardScreen {

    private final Scanner scanner = new Scanner(System.in);

    public void show(User user) {
        if (user == null || !user.isAdmin()) {
            System.out.println("⚠ Access denied. Admin role required.");
            return;
        }

        boolean keepGoing = true;

        while (keepGoing) {
            System.out.println("\n=================================");
            System.out.println("        ADMIN DASHBOARD");
            System.out.println("=================================");
            System.out.println("Logged in as: " + user.getUsername());
            System.out.println("1. Employee Management");
            System.out.println("2. Salary Management");
            System.out.println("3. Reports");
            System.out.println("4. System Admin Tools");
            System.out.println("5. Logout to main console");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> new EmployeeManagementScreen().show(user);
                case "2" -> new SalaryManagementScreen().show(user);
                case "3" -> new ReportsScreen().show(user);
                case "4" -> new SystemAdminScreen().show(user);
                case "5" -> {
                    System.out.println("Logging out of admin dashboard...");
                    keepGoing = false;
                }
                default -> System.out.println("Invalid option, try again.");
            }
        }
    }
}