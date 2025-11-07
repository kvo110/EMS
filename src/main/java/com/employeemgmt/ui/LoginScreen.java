package com.employeemgmt.ui;

import com.employeemgmt.models.User;
import com.employeemgmt.models.User.UserRole;
import com.employeemgmt.services.AuthenticationService;
import com.employeemgmt.dao.UserDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/*
   Login screen for the Employee Management System.
   Checks credentials with the database and routes users to the correct dashboard.
   Also lets users reset their password or create an account.
*/
public class LoginScreen extends Application {

    private final AuthenticationService authService = new AuthenticationService();
    private final UserDAO userDAO = new UserDAO(); // used for password reset

    @Override
    public void start(Stage primaryStage) {
        // main title
        Label titleLabel = new Label("Employee Management System");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #222;");

        // input fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label messageLabel = new Label();

        // main buttons
        Button loginBtn = new Button("Login");
        Button exitBtn = new Button("Exit");

        // extra links
        Hyperlink signupLink = new Hyperlink("Create Account");
        Hyperlink forgotLink = new Hyperlink("Forgot Password?");

        // handle login click
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter both username and password.");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            AuthenticationService.AuthenticationResult result = authService.login(username, password);

            if (result.isSuccess()) {
                messageLabel.setText("Login successful!");
                messageLabel.setStyle("-fx-text-fill: green;");
                User loggedUser = result.getUser();
                primaryStage.close();

                // route based on user role
                if (loggedUser.getRole() == UserRole.ADMIN) {
                    new AdminDashboard().start(new Stage(), loggedUser.getUsername());
                } else if (loggedUser.getRole() == UserRole.EMPLOYEE) {
                    new EmployeeDashboard().start(new Stage(), loggedUser.getUsername());
                } else {
                    showAlert("Login Error", "User role not recognized.");
                }
            } else {
                messageLabel.setText(result.getMessage());
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        });

        // exit closes the app
        exitBtn.setOnAction(e -> {
            System.out.println("Application closed by user.");
            primaryStage.close();
        });

        // open sub-windows
        signupLink.setOnAction(e -> openSignupWindow());
        forgotLink.setOnAction(e -> openForgotPasswordWindow());

        VBox layout = new VBox(15, titleLabel, usernameField, passwordField, loginBtn,
                messageLabel, signupLink, forgotLink, exitBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #f3f6fa;");

        Scene scene = new Scene(layout, 400, 380);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /*
       Forgot password screen – updates password in the DB and includes a "Back to Login" button.
    */
    private void openForgotPasswordWindow() {
        Stage stage = new Stage();
        stage.setTitle("Reset Password");

        Label info = new Label("Enter your username and new password:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        Button resetBtn = new Button("Update Password");
        Button backBtn = new Button("Back to Login");
        Label message = new Label();

        resetBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String newPass = newPasswordField.getText().trim();
            String confirmPass = confirmPasswordField.getText().trim();

            if (username.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                message.setText("Please fill out all fields.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                message.setText("Passwords do not match.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }

            try {
                boolean success = userDAO.changePassword(username, newPass);
                if (success) {
                    message.setText("Password updated successfully!");
                    message.setStyle("-fx-text-fill: green;");
                    System.out.println("Password changed for: " + username);
                } else {
                    message.setText("Username not found or update failed.");
                    message.setStyle("-fx-text-fill: red;");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Error resetting password.");
                message.setStyle("-fx-text-fill: red;");
            }
        });

        // back to login closes this window and reopens main login
        backBtn.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox layout = new VBox(15, info, usernameField, newPasswordField, confirmPasswordField,
                resetBtn, backBtn, message);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f8f8f8;");

        stage.setScene(new Scene(layout, 360, 280));
        stage.show();
    }

    /*
       Signup window – includes "Back to Login" button for convenience.
    */
    private void openSignupWindow() {
        Stage stage = new Stage();
        stage.setTitle("Create Account");

        Label title = new Label("Register New User");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        ComboBox<UserRole> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(UserRole.ADMIN, UserRole.EMPLOYEE);
        roleBox.setPromptText("Select Role");

        Button createBtn = new Button("Create Account");
        Button backBtn = new Button("Back to Login");
        Label message = new Label();

        createBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            UserRole role = roleBox.getValue();

            if (username.isEmpty() || password.isEmpty() || role == null) {
                message.setText("All fields are required.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }

            AuthenticationService.UserCreationResult result =
                    authService.createUser(username, password, role, null);

            if (result.isSuccess()) {
                message.setText("User created successfully!");
                message.setStyle("-fx-text-fill: green;");
                System.out.println("Created user: " + username + " (" + role + ")");
            } else {
                message.setText("Error: " + result.getMessage());
                message.setStyle("-fx-text-fill: red;");
            }
        });

        backBtn.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox layout = new VBox(15, title, usernameField, passwordField, roleBox,
                createBtn, backBtn, message);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #f8f9fc;");

        stage.setScene(new Scene(layout, 400, 320));
        stage.show();
    }

    // simple popup alert helper
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}