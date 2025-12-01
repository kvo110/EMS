package com.employeemgmt.ui.fx;

import com.employeemgmt.models.User.UserRole;
import com.employeemgmt.services.AuthenticationService;
import com.employeemgmt.services.AuthenticationService.UserCreationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
    RegisterScreen
    --------------
    Basic "create account" screen.

    Notes:
    - For EMPLOYEE accounts, an existing empid is required
      (admin should create the employee row first in CRUD).
    - For ADMIN accounts, empid is optional (we just pass null).
*/
public class RegisterScreen {

    private final AuthenticationService authService = new AuthenticationService();

    public void start(Stage stage) {
        Label title = new Label("Create New Account");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Employee", "Admin");
        roleBox.setValue("Employee");

        TextField empIdField = new TextField();
        empIdField.setPromptText("Employee ID (required for Employee)");

        Label message = new Label();
        message.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button createBtn = new Button("Create Account");
        Button backBtn = new Button("Back to Login");
        createBtn.setPrefWidth(200);
        backBtn.setPrefWidth(200);

        createBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirm = confirmField.getText().trim();
            String roleStr = roleBox.getValue();
            String empIdText = empIdField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                message.setText("Please fill out username and both password fields.");
                return;
            }

            if (!password.equals(confirm)) {
                message.setText("Passwords do not match.");
                return;
            }

            if (roleStr == null) {
                message.setText("Please select a role.");
                return;
            }

            UserRole role = "Admin".equals(roleStr) ? UserRole.ADMIN : UserRole.EMPLOYEE;

            Integer empid = null;
            if (role == UserRole.EMPLOYEE) {
                if (empIdText.isEmpty()) {
                    message.setText("Employee ID is required for Employee role.");
                    return;
                }
                try {
                    empid = Integer.parseInt(empIdText);
                } catch (NumberFormatException ex) {
                    message.setText("Employee ID must be a number.");
                    return;
                }
            }

            UserCreationResult result = authService.createUser(username, password, role, empid);

            if (!result.isSuccess()) {
                message.setText(result.getMessage());
                return;
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Account Created");
            alert.setHeaderText(null);
            alert.setContentText("Account created successfully. You can log in now.");
            alert.showAndWait();

            stage.close();
            new LoginScreen().start(new Stage());
        });

        backBtn.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox layout = new VBox(
                10,
                title,
                usernameField,
                passwordField,
                confirmField,
                roleBox,
                empIdField,
                createBtn,
                backBtn,
                message
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(28));
        layout.setStyle("-fx-background-color: #f5f9ff;");

        Scene scene = new Scene(layout, 480, 420);
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }
}
