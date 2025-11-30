package com.employeemgmt.ui.console.Admin;

import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;

import java.util.Scanner;

/*
   SalaryManagementScreen
   ----------------------
   Tiny console wrapper around the EmployeeService
   salary range update feature.
*/
public class SalaryManagementScreen {

    private final Scanner scanner = new Scanner(System.in);
    private final EmployeeService employeeService = new EmployeeService();

    public void show(User admin) {
        if (admin == null || !admin.isAdmin()) {
            System.out.println("Only admins can update salaries.");
            return;
        }

        System.out.println("\n--- Salary Management (Console) ---");
        try {
            System.out.print("Min salary: ");
            double min = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Max salary: ");
            double max = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Increase percentage (e.g. 3.5): ");
            double pct = Double.parseDouble(scanner.nextLine().trim());

            var result = employeeService.updateSalaryRange(pct, min, max, admin);
            if (result.isSuccess()) {
                System.out.println("Done: " + result.getMessage());
            } else {
                System.out.println("Error: " + result.getMessage());
            }

        } catch (NumberFormatException ex) {
            System.out.println("Bad number format, try again.");
        }
    }
}