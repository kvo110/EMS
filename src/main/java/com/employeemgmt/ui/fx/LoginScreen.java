package com.employeemgmt.ui.fx;

import com.employeemgmt.models.User;
import com.employeemgmt.services.AuthenticationService;
import com.employeemgmt.services.AuthenticationService.AuthenticationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Simple login window for the EMS app.
// Uses AuthenticationService to check username/password.
public class LoginScreen {

    private final AuthenticationService authService = new AuthenticationService();

    public void start(Stage stage) {
        Label title = new Label("Employee Management System");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Just a small hint for the user about what they intend
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Employee", "Admin");
        roleBox.setValue("Employee");

        Label message = new Label();
        message.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(200);

        Button registerBtn = new Button("Register");
        registerBtn.setPrefWidth(200);

        // Login button handler
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String pickedRole = roleBox.getValue();

            if (username.isEmpty() || password.isEmpty()) {
                message.setText("Please enter both username and password.");
                return;
            }

            AuthenticationResult result = authService.login(username, password);

            if (!result.isSuccess()) {
                message.setText(result.getMessage());
                return;
            }

            User loggedIn = result.getUser();

            // Light guard: if user picked "Admin" but is not admin
            if ("Admin".equals(pickedRole) && !loggedIn.isAdmin()) {
                message.setText("This account is not an admin.");
                return;
            }

            if ("Employee".equals(pickedRole) && loggedIn.isAdmin()) {
                message.setText("This account is an admin. Pick Admin above.");
                return;
            }

            stage.close();

            if (loggedIn.isAdmin()) {
                new AdminDashboard().start(new Stage(), loggedIn);
            } else {
                new EmployeeDashboard().start(new Stage(), loggedIn);
            }
        });

        // Register goes to the account creation screen
        registerBtn.setOnAction(e -> {
            stage.close();
            new RegisterScreen().start(new Stage());
        });

        VBox root = new VBox(
                12,
                title,
                usernameField,
                passwordField,
                roleBox,
                loginBtn,
                registerBtn,
                message
        );
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f7faff;");

        Scene scene = new Scene(root, 420, 380);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
