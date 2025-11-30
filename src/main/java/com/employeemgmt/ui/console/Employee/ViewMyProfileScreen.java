package com.employeemgmt.ui.console.Employee;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;

import java.time.format.DateTimeFormatter;

/*
   ViewMyProfileScreen
   -------------------
   Loads the employee record for the logged-in user and
   prints out basic info in the console.
*/
public class ViewMyProfileScreen {

    private final EmployeeService employeeService = new EmployeeService();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public void show(User user) {
        if (user == null || user.getEmpid() == null || user.getEmpid() <= 0) {
            System.out.println("Your account is not linked to an employee record.");
            return;
        }

        var result = employeeService.searchEmployeeById(user.getEmpid(), user);
        if (!result.isSuccess() || result.getEmployees().isEmpty()) {
            System.out.println("Could not load your profile: " + result.getMessage());
            return;
        }

        Employee e = result.getEmployees().get(0);

        System.out.println("\n--- My Profile ---");
        System.out.println("Employee ID: " + e.getEmpid());
        System.out.println("Name      : " + e.getFullName());
        System.out.println("Email     : " + e.getEmail());
        System.out.println("SSN       : " + (e.getSsn() == null ? "N/A" : "***-**-" + e.getSsn().substring(e.getSsn().length() - 4)));

        if (e.getHireDate() != null) {
            System.out.println("Hire Date : " + e.getHireDate().format(DATE_FMT));
        } else {
            System.out.println("Hire Date : N/A");
        }

        System.out.println("Salary    : " + e.getFormattedSalary());
    }
}