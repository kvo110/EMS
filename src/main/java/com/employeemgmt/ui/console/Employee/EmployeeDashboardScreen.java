package com.employeemgmt.ui.console.Employee;

import com.employeemgmt.models.User;

import java.util.Scanner;

/*
   EmployeeDashboardScreen
   -----------------------
   Main console menu for regular employees.
   From here they can view their profile, pay history,
   or update their contact info.
*/
public class EmployeeDashboardScreen {

    private final Scanner scanner = new Scanner(System.in);

    public void show(User user) {
        if (user == null || user.isAdmin()) {
            System.out.println("This dashboard is only for non-admin employees.");
            return;
        }

        boolean keepGoing = true;

        while (keepGoing) {
            System.out.println("\n========== Employee Dashboard ==========");
            System.out.println("Hello, " + user.getUsername());
            System.out.println("1. View My Profile");
            System.out.println("2. View My Pay History");
            System.out.println("3. Update My Contact Info");
            System.out.println("4. Logout");
            System.out.println("5. Exit Program");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> new ViewMyProfileScreen().show(user);
                case "2" -> new PayHistoryScreen().show(user);
                case "3" -> new UpdateContactInfoScreen().show(user);
                case "4" -> {
                    System.out.println("Logging out...");
                    keepGoing = false;
                }
                case "5" -> {
                    System.out.println("Exiting program. Bye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option, try again.");
            }
        }
    }
}