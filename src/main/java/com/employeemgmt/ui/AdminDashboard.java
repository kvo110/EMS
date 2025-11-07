package com.employeemgmt.ui;

import com.employeemgmt.services.AuthenticationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/*
   Admin Dashboard GUI
   Shows the main admin options and now properly handles logout.
   When the admin logs out, it clears their AuthenticationService session.
*/
public class AdminDashboard {

    // keeps track of the current authentication session
    private final AuthenticationService authService = new AuthenticationService();

    public void start(Stage stage, String username) {
        // welcome section
        Label welcomeLabel = new Label("Welcome, " + username + " (HR Admin)");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label infoLabel = new Label("Choose a section below to manage the system:");
        infoLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        // dashboard buttons
        Button manageEmployeesBtn = new Button("👥 Manage Employees");
        Button payrollBtn = new Button("💰 View & Edit Payroll");
        Button reportsBtn = new Button("📊 Generate Reports");
        Button settingsBtn = new Button("⚙️ System Settings");
        Button logoutBtn = new Button("🚪 Logout");

        // make all buttons look clean and uniform
        for (Button btn : new Button[]{manageEmployeesBtn, payrollBtn, reportsBtn, settingsBtn, logoutBtn}) {
            btn.setPrefWidth(200);
            btn.setStyle("-fx-font-size: 14px;");
        }

        // when admin clicks "Manage Employees"
        manageEmployeesBtn.setOnAction(e -> {
            stage.close();
            new ManageEmployeesScreen().start(new Stage(), username);
        });

        payrollBtn.setOnAction(e -> showPlaceholder("Payroll Viewer"));
        reportsBtn.setOnAction(e -> showPlaceholder("Reports Generator"));
        settingsBtn.setOnAction(e -> showPlaceholder("System Settings"));

        // logout clears user session and returns to login screen
        logoutBtn.setOnAction(e -> {
            try {
                // clear authentication session
                authService.logout();
                System.out.println("Admin logged out successfully.");
            } catch (Exception ex) {
                System.out.println("Error logging out: " + ex.getMessage());
            }

            // close current dashboard and reopen login screen
            stage.close();
            new LoginScreen().start(new Stage());
        });

        // layout setup
        VBox layout = new VBox(15, welcomeLabel, infoLabel,
                manageEmployeesBtn, payrollBtn, reportsBtn, settingsBtn, logoutBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #eef5ff;");

        Scene scene = new Scene(layout, 500, 400);
        stage.setTitle("Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    // just a simple placeholder popup until those features are ready
    private void showPlaceholder(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(featureName);
        alert.setHeaderText(null);
        alert.setContentText(featureName + " feature is under development.");
        alert.showAndWait();
    }
}