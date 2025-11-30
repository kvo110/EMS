package com.employeemgmt.ui.fx;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
   EmployeeDashboard
   Simple home screen for regular employees.
*/
public class EmployeeDashboard {

    private final EmployeeService employeeService = new EmployeeService();

    public void start(Stage stage, User user) {
        if (user == null || user.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label title = new Label("Welcome, " + user.getUsername());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea profileArea = new TextArea();
        profileArea.setEditable(false);
        profileArea.setPrefRowCount(8);

        Button refreshProfileBtn = new Button("Refresh My Profile");
        Button logoutBtn = new Button("Logout");

        refreshProfileBtn.setOnAction(e -> {
            SearchResult result = employeeService.searchEmployeeById(user.getEmpid(), user);

            if (!result.isSuccess() || result.getEmployees().isEmpty()) {
                profileArea.setText("Could not load your employee profile.\n" + result.getMessage());
                return;
            }

            Employee emp = result.getEmployees().get(0);
            StringBuilder sb = new StringBuilder();
            sb.append("Employee ID: ").append(emp.getEmpid()).append("\n");
            sb.append("Name: ").append(emp.getFullName()).append("\n");
            sb.append("Email: ").append(emp.getEmail()).append("\n");
            sb.append("Hire Date: ").append(emp.getHireDate()).append("\n");
            sb.append("Base Salary: ").append(emp.getFormattedSalary()).append("\n");
            profileArea.setText(sb.toString());
        });

        logoutBtn.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox layout = new VBox(
                12,
                title,
                profileArea,
                refreshProfileBtn,
                logoutBtn
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #f5f9ff;");

        Scene scene = new Scene(layout, 520, 380);
        stage.setTitle("Employee Dashboard");
        stage.setScene(scene);
        stage.show();

        refreshProfileBtn.fire();
    }
}