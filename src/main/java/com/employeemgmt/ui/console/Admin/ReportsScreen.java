package com.employeemgmt.ui.console.Admin;

import com.employeemgmt.models.User;
import com.employeemgmt.services.ReportService;

import java.util.Scanner;

/*
   ReportsScreen
   -------------
   Just a simple placeholder that calls a couple of
   report service methods (or at least shows how it
   would be wired).
*/
public class ReportsScreen {

    private final Scanner scanner = new Scanner(System.in);
    private final ReportService reportService = new ReportService();

    public void show(User admin) {
        if (admin == null || !admin.isAdmin()) {
            System.out.println("Reports are only for admins.");
            return;
        }

        System.out.println("\n--- Reports (Console) ---");
        System.out.println("1. Total Pay by Job Title");
        System.out.println("2. Total Pay by Division");
        System.out.println("3. Back");
        System.out.print("Pick an option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> System.out.println("Report: Total pay by job title (use ReportService in real build).");
            case "2" -> System.out.println("Report: Total pay by division (use ReportService in real build).");
            default -> System.out.println("Going back.");
        }
    }
}