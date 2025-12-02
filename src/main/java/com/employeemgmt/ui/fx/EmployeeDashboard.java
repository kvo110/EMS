package com.employeemgmt.ui.fx;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.PayStatement;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.PayHistoryResult;
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
    EmployeeDashboard (FX)
    ----------------------
    For regular employees only.

    - shows their basic profile
    - shows their pay history (newest first)
*/
public class EmployeeDashboard {

    private final EmployeeService employeeService = new EmployeeService();

    public void start(Stage stage, User user) {
        // only regular employees should land here
        if (user == null || user.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label title = new Label("Welcome, " + user.getUsername());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea profileArea = new TextArea();
        profileArea.setEditable(false);
        profileArea.setPrefRowCount(6);

        TextArea payHistoryArea = new TextArea();
        payHistoryArea.setEditable(false);
        payHistoryArea.setPrefRowCount(12);

        Button refreshProfileBtn = new Button("Load My Profile");
        Button payHistoryBtn = new Button("Load My Pay History");
        Button logoutBtn = new Button("Logout");

        // load and show basic profile
        refreshProfileBtn.setOnAction(e -> {
            SearchResult r = employeeService.searchEmployeeById(user.getEmpid(), user);
            if (!r.isSuccess() || r.getEmployees().isEmpty()) {
                profileArea.setText("Could not load your profile.\n" + r.getMessage());
                return;
            }

            Employee emp = r.getEmployees().get(0);

            StringBuilder sb = new StringBuilder();
            sb.append("Employee ID: ").append(emp.getEmpid()).append("\n");
            sb.append("Name: ").append(emp.getFullName()).append("\n");
            sb.append("Email: ").append(emp.getEmail()).append("\n");
            sb.append("Hire Date: ").append(emp.getHireDate()).append("\n");
            sb.append("Base Salary: ").append(emp.getFormattedSalary()).append("\n");

            profileArea.setText(sb.toString());

            // make sure pay history exists for this employee
            employeeService.ensurePayHistory(emp);
        });

        // load and show pay history, newest first
        payHistoryBtn.setOnAction(e -> {
            PayHistoryResult res =
                    employeeService.getPayHistoryForEmployee(user.getEmpid(), user);

            if (!res.isSuccess()) {
                payHistoryArea.setText(res.getMessage());
                return;
            }

            if (res.getHistory().isEmpty()) {
                payHistoryArea.setText("No pay history found.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Pay History (most recent first)\n");
            sb.append("--------------------------------\n");

            for (PayStatement p : res.getHistory()) {
                sb.append(p.getPayDate())
                        .append(" | Gross: ")
                        .append(p.getGrossPay())
                        .append(" | Net: ")
                        .append(p.getNetPay())
                        .append("\n");
            }

            payHistoryArea.setText(sb.toString());
        });

        logoutBtn.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox root = new VBox(
                10,
                title,
                refreshProfileBtn,
                profileArea,
                payHistoryBtn,
                payHistoryArea,
                logoutBtn
        );
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f9ff;");

        Scene scene = new Scene(root, 650, 550);
        stage.setTitle("Employee Dashboard");
        stage.setScene(scene);
        stage.show();

        // auto-load profile once on open so the screen doesn't look empty
        refreshProfileBtn.fire();
    }
}
