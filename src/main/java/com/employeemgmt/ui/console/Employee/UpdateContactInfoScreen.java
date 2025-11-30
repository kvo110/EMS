package com.employeemgmt.ui.console.Employee;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;

import java.util.Scanner;

/*
   UpdateContactInfoScreen
   -----------------------
   Lets an employee update their email in the database.
   Phone number is in the model but not in the current table,
   so for now I'm just wiring email so it actually persists.
*/
public class UpdateContactInfoScreen {

    private final Scanner scanner = new Scanner(System.in);
    private final EmployeeService employeeService = new EmployeeService();

    public void show(User user) {
        if (user == null || user.getEmpid() == null || user.getEmpid() <= 0) {
            System.out.println("Your user isn't linked to an employee record.");
            return;
        }

        var searchResult = employeeService.searchEmployeeById(user.getEmpid(), user);
        if (!searchResult.isSuccess() || searchResult.getEmployees().isEmpty()) {
            System.out.println("Couldn't load your current info: " + searchResult.getMessage());
            return;
        }

        Employee emp = searchResult.getEmployees().get(0);

        System.out.println("\n--- Update Contact Info ---");
        System.out.println("Current email: " + emp.getEmail());
        System.out.print("New email (leave blank to keep current): ");
        String newEmail = scanner.nextLine().trim();

        if (!newEmail.isEmpty()) {
            emp.setEmail(newEmail);
        }

        var updateResult = employeeService.updateEmployee(emp, user);
        if (updateResult.isSuccess()) {
            System.out.println("Contact info updated successfully.");
        } else {
            System.out.println("Could not save changes: " + updateResult.getMessage());
        }
    }
}