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
    --------------
    Home screen for HR Admin accounts.

    Buttons:
    - Manage Employees (CRUD screen)
    - Salary Tools (raise helpers)
    - Reports (basic text reports)
    - Logout
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

        Label hint = new Label("Choose a tool to open:");
        hint.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");

        Button manageEmployeesBtn = new Button("👥 Manage Employees");
        Button salaryToolsBtn = new Button("💰 Salary Tools");
        Button reportsBtn = new Button("📊 Reports");
        Button logoutBtn = new Button("🚪 Logout");

        Button[] buttons = { manageEmployeesBtn, salaryToolsBtn, reportsBtn, logoutBtn };
        for (Button b : buttons) {
            b.setPrefWidth(260);
            b.setStyle("-fx-font-size: 14px;");
        }

        manageEmployeesBtn.setOnAction(e -> {
            stage.close();
            new ManagementEmployeesScreen().start(new Stage(), adminUser);
        });

        salaryToolsBtn.setOnAction(e -> {
            stage.close();
            new SalaryToolsScreen().start(new Stage(), adminUser);
        });

        reportsBtn.setOnAction(e -> {
            stage.close();
            new ReportsScreen().start(new Stage(), adminUser);
        });

        logoutBtn.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox layout = new VBox(
                15,
                welcome,
                hint,
                manageEmployeesBtn,
                salaryToolsBtn,
                reportsBtn,
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
