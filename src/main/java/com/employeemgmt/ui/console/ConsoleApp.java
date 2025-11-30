package com.employeemgmt.ui.console;

import com.employeemgmt.models.User;
import com.employeemgmt.services.AuthenticationService;
import com.employeemgmt.ui.console.Login.LoginScreen;
import com.employeemgmt.ui.console.Admin.AdminDashboardScreen;
import com.employeemgmt.ui.console.Employee.EmployeeDashboardScreen;

/*
   ConsoleApp
   ----------
   This is the "launcher" for the console version of EMS.
   It just handles login, then sends the user to either
   the admin dashboard or the employee dashboard.
*/
public class ConsoleApp {

    // service that actually talks to the database for auth
    private final AuthenticationService authService = new AuthenticationService();

    public ConsoleApp() {
        // nothing special here, just wiring stuff up
    }

    // entry point for the console UI (called from Main.java)
    public void start() {
        System.out.println("==== Employee Management System (Console) ====");

        // text-based login flow
        LoginScreen loginScreen = new LoginScreen(authService);
        User loggedInUser = loginScreen.show();

        // if login failed or user bailed out
        if (loggedInUser == null) {
            System.out.println("Exiting console mode. Bye.");
            return;
        }

        // route based on role
        if (loggedInUser.isAdmin()) {
            AdminDashboardScreen adminUI = new AdminDashboardScreen();
            adminUI.show(loggedInUser);
        } else {
            EmployeeDashboardScreen empUI = new EmployeeDashboardScreen();
            empUI.show(loggedInUser);
        }

        System.out.println("Thanks for using EMS (console version).");
    }
}