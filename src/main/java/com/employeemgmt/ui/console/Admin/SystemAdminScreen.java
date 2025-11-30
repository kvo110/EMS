package com.employeemgmt.ui.console.Admin;

import com.employeemgmt.models.User;

import java.util.Scanner;

/*
   SystemAdminScreen
   -----------------
   Placeholder for system-level tools like:
   - creating users
   - resetting passwords
   - seeding sample data, etc.
*/
public class SystemAdminScreen {

    private final Scanner scanner = new Scanner(System.in);

    public void show(User admin) {
        if (admin == null || !admin.isAdmin()) {
            System.out.println("System tools are admin-only.");
            return;
        }

        System.out.println("\n--- System Admin Tools (Console) ---");
        System.out.println("(These are mostly stubs for now.)");
        System.out.println("1. Show environment info");
        System.out.println("2. Back");
        System.out.print("Choice: ");

        String choice = scanner.nextLine().trim();
        if ("1".equals(choice)) {
            System.out.println("Java version: " + System.getProperty("java.version"));
            System.out.println("User: " + System.getProperty("user.name"));
        } else {
            System.out.println("Returning to admin dashboard.");
        }
    }
}