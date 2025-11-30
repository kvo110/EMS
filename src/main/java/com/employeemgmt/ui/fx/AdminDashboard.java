package com.employeemgmt.ui.fx;

import com.employeemgmt.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
   AdminDashboard
   Landing page for HR Admins after login.
*/
public class AdminDashboard {

    public void start(Stage stage, User adminUser) {
        if (adminUser == null || !adminUser.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label welcome = new Label("Welcome, " + adminUser.getUsername() + " (HR Admin)");
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label info = new Label("Pick what you want to manage:");
        info.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");

        Button manageEmployeesBtn = new Button("Manage Employees");
        Button salaryBtn = new Button("Salary Tools (Console / DAO only for now)");
        Button logoutBtn = new Button("Logout");

        for (Button b : new Button[]{manageEmployeesBtn, salaryBtn, logoutBtn}) {
            b.setPrefWidth(260);
            b.setStyle("-fx-font-size: 14px;");
        }

        manageEmployeesBtn.setOnAction(e -> {
            stage.close();
            new ManagementEmployeesScreen().start(new Stage(), adminUser);
        });

        salaryBtn.setOnAction(e ->
                System.out.println("[FX] Salary update features are handled via EmployeeService."));

        logoutBtn.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox layout = new VBox(
                15,
                welcome,
                info,
                manageEmployeesBtn,
                salaryBtn,
                logoutBtn
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #eef5ff;");

        Scene scene = new Scene(layout, 520, 380);
        stage.setTitle("Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}