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
    Home screen for HR admins.

    From here an admin can:
    - manage employees (full CRUD)
    - open salary tools (raises, ranges, etc.)
    - open reports (basic reporting screen)
    - log out back to the login screen

    I kept this layout super simple on purpose so it is easy
    to demo in class and talk through each feature.
*/
public class AdminDashboard {

    public void start(Stage stage, User adminUser) {
        // quick safety check so a non-admin cannot end up here
        if (adminUser == null || !adminUser.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label welcome = new Label("Welcome, " + adminUser.getUsername() + " (HR Admin)");
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label info = new Label("Choose a tool to open:");
        info.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");

        // main dashboard buttons
        Button manageEmployeesBtn = new Button("👥 Manage Employees");
        Button salaryToolsBtn     = new Button("💰 Salary Tools");
        Button reportsBtn         = new Button("📊 Reports");
        Button settingsBtn        = new Button("⚙️ Settings (placeholder)");
        Button logoutBtn          = new Button("🚪 Logout");

        // make the buttons feel consistent
        for (Button b : new Button[]{manageEmployeesBtn, salaryToolsBtn, reportsBtn, settingsBtn, logoutBtn}) {
            b.setPrefWidth(240);
            b.setStyle("-fx-font-size: 14px;");
        }

        // open the employee CRUD screen
        manageEmployeesBtn.setOnAction(e -> {
            stage.close();
            new ManagementEmployeesScreen().start(new Stage(), adminUser);
        });

        // open the salary tools screen (raises / ranges)
        salaryToolsBtn.setOnAction(e -> {
            stage.close();
            new SalaryToolsScreen().start(new Stage(), adminUser);
        });

        // open the reports screen (all employees + salary summary)
        reportsBtn.setOnAction(e -> {
            stage.close();
            new ReportsScreen().start(new Stage(), adminUser);
        });

        // still a placeholder, so just log something for now
        settingsBtn.setOnAction(e -> {
            System.out.println("[FX] Settings screen is not implemented yet.");
        });

        // send admin back to login
        logoutBtn.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox layout = new VBox(
                15,
                welcome,
                info,
                manageEmployeesBtn,
                salaryToolsBtn,
                reportsBtn,
                settingsBtn,
                logoutBtn
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #eef5ff;");

        Scene scene = new Scene(layout, 520, 400);
        stage.setTitle("Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
