package com.employeemgmt.ui.fx;

import com.employeemgmt.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
   Admin home page.
   Added:
   - Salary Tools (all employees or specific range)
*/
public class AdminDashboard {

    public void start(Stage stage, User admin) {
        if (admin == null || !admin.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label title = new Label("👤 Admin Dashboard");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Button manage = new Button("👥 Manage Employees");
        Button salaryTools = new Button("💰 Salary Tools");
        Button logout = new Button("🚪 Logout");

        manage.setPrefWidth(220);
        salaryTools.setPrefWidth(220);
        logout.setPrefWidth(220);

        manage.setOnAction(e -> {
            stage.close();
            new ManagementEmployeesScreen().start(new Stage(), admin);
        });

        salaryTools.setOnAction(e -> {
            stage.close();
            new SalaryToolsScreen().start(new Stage(), admin);
        });

        logout.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox layout = new VBox(15, title, manage, salaryTools, logout);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(layout, 520, 420));
        stage.setTitle("Admin Dashboard");
        stage.show();
    }
}
