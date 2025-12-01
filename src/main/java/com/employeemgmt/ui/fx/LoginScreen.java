package com.employeemgmt.ui.fx;

import com.employeemgmt.services.AuthenticationService;
import com.employeemgmt.services.AuthenticationService.AuthenticationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/*
    LoginScreen
    -----------
    EMS login page for both Admin and Employee.

    Features:
    - username + password
    - show password toggle
    - role dropdown (Employee / Admin)
    - button to go to RegisterScreen
*/
public class LoginScreen {

    private final AuthenticationService authService = new AuthenticationService();

    public void start(Stage stage) {
        Label title = new Label("Employee Management System");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        // password field (hidden characters)
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // plain text field for "show password" mode
        TextField passwordVisibleField = new TextField();
        passwordVisibleField.setPromptText("Password");
        passwordVisibleField.setManaged(false);
        passwordVisibleField.setVisible(false);

        // checkbox to toggle visibility
        CheckBox showPasswordCheck = new CheckBox("Show password");

        // when toggled, switch which field is visible
        showPasswordCheck.setOnAction(e -> {
            if (showPasswordCheck.isSelected()) {
                // switch to visible field
                passwordVisibleField.setText(passwordField.getText());
                passwordVisibleField.setManaged(true);
                passwordVisibleField.setVisible(true);

                passwordField.setManaged(false);
                passwordField.setVisible(false);
            } else {
                // switch back to hidden field
                passwordField.setText(passwordVisibleField.getText());
                passwordField.setManaged(true);
                passwordField.setVisible(true);

                passwordVisibleField.setManaged(false);
                passwordVisibleField.setVisible(false);
            }
        });

        VBox passwordBox = new VBox(4, passwordField, passwordVisibleField, showPasswordCheck);

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Employee", "Admin");
        roleBox.setValue("Employee");

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(180);

        Button registerBtn = new Button("Register");
        registerBtn.setPrefWidth(180);

        Label message = new Label();
        message.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // main login logic
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = showPasswordCheck.isSelected()
                    ? passwordVisibleField.getText().trim()
                    : passwordField.getText().trim();
            String chosenRole = roleBox.getValue();

            if (username.isEmpty() || password.isEmpty()) {
                message.setText("Please enter both username and password.");
                return;
            }

            if (chosenRole == null) {
                message.setText("Please select a role.");
                return;
            }

            AuthenticationResult result = authService.login(username, password);

            if (!result.isSuccess()) {
                message.setText(result.getMessage());
                return;
            }

            // final role check (backend still decides the real role)
            if ("Admin".equals(chosenRole) && !result.getUser().isAdmin()) {
                message.setText("This account is not an admin.");
                return;
            }
            if ("Employee".equals(chosenRole) && result.getUser().isAdmin()) {
                message.setText("This account is an admin (choose Admin in the dropdown).");
                return;
            }

            stage.close();

            if (result.getUser().isAdmin()) {
                new AdminDashboard().start(new Stage(), result.getUser());
            } else {
                new EmployeeDashboard().start(new Stage(), result.getUser());
            }
        });

        registerBtn.setOnAction(e -> {
            stage.close();
            new RegisterScreen().start(new Stage());
        });

        VBox layout = new VBox(
                12,
                title,
                usernameField,
                passwordBox,
                roleBox,
                new HBox(10, loginBtn, registerBtn),
                message
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #f7faff;");

        Scene scene = new Scene(layout, 460, 360);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
