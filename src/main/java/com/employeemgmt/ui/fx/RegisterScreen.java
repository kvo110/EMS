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
   Simple registration:
   - Admin accounts get no empid
   - Employee accounts require existing empid in DB
*/
public class RegisterScreen {

    private final AuthenticationService auth = new AuthenticationService();

    public void start(Stage stage) {

        Label title = new Label("📝 Create Account");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField user = new TextField();
        user.setPromptText("Username");

        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");

        PasswordField confirm = new PasswordField();
        confirm.setPromptText("Confirm Password");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Employee", "Admin");
        roleBox.setValue("Employee");

        TextField empId = new TextField();
        empId.setPromptText("Employee ID (Employee role only)");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: red;");

        Button create = new Button("Create Account");
        Button back = new Button("Back");

        create.setOnAction(e -> {
            String u = user.getText().trim();
            String p = pass.getText().trim();
            String c = confirm.getText().trim();

            if (!p.equals(c)) {
                msg.setText("Passwords do not match.");
                return;
            }

            UserRole role = roleBox.getValue().equals("Admin") ? UserRole.ADMIN : UserRole.EMPLOYEE;

            Integer id = null;
            if (role == UserRole.EMPLOYEE) {
                try {
                    id = Integer.parseInt(empId.getText().trim());
                } catch (Exception ex) {
                    msg.setText("Employee ID must be a number.");
                    return;
                }
            }

            UserCreationResult result = auth.createUser(u, p, role, id);

            if (!result.isSuccess()) {
                msg.setText(result.getMessage());
                return;
            }

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setContentText("Account created. Log in now.");
            ok.showAndWait();

            stage.close();
            new LoginScreen().start(new Stage());
        });

        back.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox layout = new VBox(12,
                title, user, pass, confirm, roleBox, empId, create, back, msg
        );

        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(layout, 450, 420));
        stage.setTitle("Register");
        stage.show();
    }
}
