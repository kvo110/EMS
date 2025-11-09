package com.employeemgmt.ui;

import com.employeemgmt.services.AuthenticationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/*
   Employee Dashboard GUI
   Gives employees access to their info and now logs out properly through AuthenticationService.
*/
public class EmployeeDashboard {

    // create a reference to the authentication service to handle logout
    private final AuthenticationService authService = new AuthenticationService();

    public void start(Stage stage, String username) {
        // welcome header
        Label welcomeLabel = new Label("Welcome, " + username);
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label infoLabel = new Label("You are logged in as an Employee.");
        infoLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        // action buttons
        Button viewProfileBtn = new Button("👤 View My Profile");
        Button viewPayrollBtn = new Button("💰 View Payroll History");
        Button updateInfoBtn = new Button("✏️ Update Personal Info");
        Button helpBtn = new Button("❓ Help & Support");
        Button logoutBtn = new Button("🚪 Logout");

        // make all buttons look the same
        for (Button btn : new Button[]{viewProfileBtn, viewPayrollBtn, updateInfoBtn, helpBtn, logoutBtn}) {
            btn.setPrefWidth(200);
            btn.setStyle("-fx-font-size: 14px;");
        }

        // placeholder actions (until features are added)
        viewProfileBtn.setOnAction(e -> showPlaceholder("Profile Viewer"));
        viewPayrollBtn.setOnAction(e -> showPlaceholder("Payroll History"));
        updateInfoBtn.setOnAction(e -> showPlaceholder("Update Info"));
        helpBtn.setOnAction(e -> showPlaceholder("Help & Support"));

        // logout: clears session and goes back to login
        logoutBtn.setOnAction(e -> {
            try {
                authService.logout(); // end current session
                System.out.println("Employee logged out successfully.");
            } catch (Exception ex) {
                System.out.println("Error logging out: " + ex.getMessage());
            }

            stage.close();
            new LoginScreen().start(new Stage());
        });

        // main layout setup
        VBox layout = new VBox(15, welcomeLabel, infoLabel, viewProfileBtn,
                viewPayrollBtn, updateInfoBtn, helpBtn, logoutBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #eaf7ea;");

        Scene scene = new Scene(layout, 500, 400);
        stage.setTitle("Employee Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    // reusable popup for simple placeholder features
    private void showPlaceholder(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(featureName);
        alert.setHeaderText(null);
        alert.setContentText(featureName + " feature is under development.");
        alert.showAndWait();
    }
}