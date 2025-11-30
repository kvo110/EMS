package com.employeemgmt.ui.console.Login;

import com.employeemgmt.models.User;
import com.employeemgmt.services.AuthenticationService;
import com.employeemgmt.services.AuthenticationService.AuthenticationResult;

import java.util.Scanner;

/*
   LoginScreen (console)
   ----------------------
   Simple text-based login prompt.
   Asks for username + password and uses AuthenticationService
   to check them against the database.
*/
public class LoginScreen {

    private final AuthenticationService authService;
    private final Scanner scanner;

    public LoginScreen(AuthenticationService authService) {
        this.authService = authService;
        this.scanner = new Scanner(System.in);
    }

    // returns a logged-in User, or null if they give up/fail
    public User show() {
        int attempts = 0;
        int maxAttempts = 3;

        while (attempts < maxAttempts) {
            System.out.print("\nUsername: ");
            String username = scanner.nextLine().trim();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            AuthenticationResult result = authService.login(username, password);

            if (result.isSuccess()) {
                System.out.println("Login OK. Welcome " + result.getUser().getUsername() + "!");
                return result.getUser();
            } else {
                attempts++;
                System.out.println("Login failed: " + result.getMessage());
                System.out.println("Attempts left: " + (maxAttempts - attempts));
            }
        }

        System.out.println("Too many failed attempts.");
        return null;
    }
}