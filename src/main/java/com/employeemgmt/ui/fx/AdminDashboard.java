package com.employeemgmt.ui.fx;

import com.employeemgmt.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Home screen for HR Admins.
// Right now the main real button is "Manage Employees".
public class AdminDashboard {

    public void start(Stage stage, User adminUser) {
        if (adminUser == null || !adminUser.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label welcome = new Label("Welcome, " + adminUser.getUsername() + " (HR Admin)");
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label info = new Label("Choose what you want to work on:");
        info.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");

        Button manageEmployeesBtn = new Button("Manage Employees");
        Button salaryBtn = new Button("Salary Tools (coming later)");
        Button reportsBtn = new Button("Reports (coming later)");
        Button logoutBtn = new Button("Logout");

        manageEmployeesBtn.setPrefWidth(220);
        salaryBtn.setPrefWidth(220);
        reportsBtn.setPrefWidth(220);
        logoutBtn.setPrefWidth(220);

        manageEmployeesBtn.setOnAction(e -> {
            stage.close();
            new ManagementEmployeesScreen().start(new Stage(), adminUser);
        });

        // Just making it clear these still do nothing big yet
        salaryBtn.setOnAction(e ->
                System.out.println("[FX] Salary tools screen not implemented yet."));
        reportsBtn.setOnAction(e ->
                System.out.println("[FX] Reports screen not implemented yet."));

        logoutBtn.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox root = new VBox(
                15,
                welcome,
                info,
                manageEmployeesBtn,
                salaryBtn,
                reportsBtn,
                logoutBtn
        );
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #eef5ff;");

        Scene scene = new Scene(root, 520, 400);
        stage.setTitle("Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
