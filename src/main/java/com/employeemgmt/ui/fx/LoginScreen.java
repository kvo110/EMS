package com.employeemgmt.ui.fx;

import com.employeemgmt.services.AuthenticationService;
import com.employeemgmt.services.AuthenticationService.AuthenticationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/*
   Login screen with:
   - show password toggle
   - role selector
   - register button
   - no fancy assets, just emojis so nothing breaks
*/
public class LoginScreen {

    private final AuthenticationService auth = new AuthenticationService();

    public void start(Stage stage) {

        Label title = new Label("🔐 Employee Management System");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField hiddenPass = new PasswordField();
        hiddenPass.setPromptText("Password");

        TextField visiblePass = new TextField();
        visiblePass.setManaged(false);
        visiblePass.setVisible(false);

        CheckBox showPass = new CheckBox("Show Password");

        showPass.selectedProperty().addListener((obs, was, isNow) -> {
            if (isNow) {
                visiblePass.setText(hiddenPass.getText());
                visiblePass.setManaged(true);
                visiblePass.setVisible(true);
                hiddenPass.setManaged(false);
                hiddenPass.setVisible(false);
            } else {
                hiddenPass.setText(visiblePass.getText());
                hiddenPass.setManaged(true);
                hiddenPass.setVisible(true);
                visiblePass.setManaged(false);
                visiblePass.setVisible(false);
            }
        });

        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("Employee", "Admin");
        role.setValue("Employee");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: red;");

        Button login = new Button("➡ Login");
        login.setPrefWidth(180);

        Button register = new Button("📝 Register");
        register.setPrefWidth(180);

        login.setOnAction(e -> {
            String user = username.getText().trim();
            String pass = showPass.isSelected() ? visiblePass.getText().trim() : hiddenPass.getText().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                msg.setText("Please enter username and password.");
                return;
            }

            AuthenticationResult result = auth.login(user, pass);

            if (!result.isSuccess()) {
                msg.setText(result.getMessage());
                return;
            }

            stage.close();

            if (result.getUser().isAdmin()) {
                new AdminDashboard().start(new Stage(), result.getUser());
            } else {
                new EmployeeDashboard().start(new Stage(), result.getUser());
            }
        });

        register.setOnAction(e -> {
            stage.close();
            new RegisterScreen().start(new Stage());
        });

        VBox layout = new VBox(12,
                title,
                username,
                hiddenPass,
                visiblePass,
                showPass,
                role,
                login,
                register,
                msg
        );
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(layout, 420, 400));
        stage.setTitle("Login");
        stage.show();
    }
}
