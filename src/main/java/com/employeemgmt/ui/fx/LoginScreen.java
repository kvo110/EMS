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

/*
   LoginScreen
   Main login page for the JavaFX EMS app.

   Features:
   - Username + password
   - Role selection hint (Admin vs Employee)
   - Register button that opens RegisterScreen
*/
public class LoginScreen {

    private final AuthenticationService authService = new AuthenticationService();

    public void start(Stage stage) {

        Label title = new Label("Employee Management System");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        ComboBox<String> roleSelect = new ComboBox<>();
        roleSelect.getItems().addAll("Employee", "Admin");
        roleSelect.setValue("Employee");
        roleSelect.setPrefWidth(180);

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(180);

        Button registerBtn = new Button("Register");
        registerBtn.setPrefWidth(180);

        Label message = new Label();
        message.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String pickedRole = roleSelect.getValue();

            if (username.isEmpty() || password.isEmpty()) {
                message.setText("Please enter both username and password.");
                return;
            }

            AuthenticationResult result = authService.login(username, password);

            if (!result.isSuccess()) {
                message.setText(result.getMessage());
                return;
            }

            User user = result.getUser();
            if (user == null) {
                message.setText("Login failed. No user returned.");
                return;
            }

            // light role check just for UX
            if ("Admin".equals(pickedRole) && !user.isAdmin()) {
                message.setText("This account is not an admin.");
                return;
            }
            if ("Employee".equals(pickedRole) && user.isAdmin()) {
                message.setText("This account is an admin. Switch role to Admin.");
                return;
            }

            stage.close();

            if (user.isAdmin()) {
                new AdminDashboard().start(new Stage(), user);
            } else {
                new EmployeeDashboard().start(new Stage(), user);
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
                passwordField,
                roleSelect,
                loginBtn,
                registerBtn,
                message
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: #f7faff;");

        Scene scene = new Scene(layout, 420, 380);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}