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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;

/*
    ReportsScreen
    -------------
    Small reporting window for admins.

    Right now it has:
    - "All Employees" report  → prints each employee line by line
    - "Salary Summary" report → total salary + average salary

    This is enough to show basic reporting in the UI and matches
    the idea of a simple HR reporting tool for the project.
*/
public class ReportsScreen {

    private final EmployeeService employeeService = new EmployeeService();

    public void start(Stage stage, User adminUser) {
        // only admins should see reports
        if (adminUser == null || !adminUser.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label title = new Label("Reports");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefRowCount(16);

        Button allEmployeesBtn = new Button("All Employees");
        Button salarySummaryBtn = new Button("Salary Summary");
        Button backBtn = new Button("Back to Admin");

        // show every employee in the system
        allEmployeesBtn.setOnAction(e -> {
            SearchResult result = employeeService.getAllEmployees(adminUser);
            if (!result.isSuccess()) {
                reportArea.setText("Error: " + result.getMessage());
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("All Employees Report\n\n");
            for (Employee emp : result.getEmployees()) {
                sb.append("ID: ").append(emp.getEmpid()).append("  ");
                sb.append("Name: ").append(emp.getFullName()).append("  ");
                sb.append("Email: ").append(emp.getEmail()).append("  ");
                sb.append("Salary: ").append(emp.getFormattedSalary()).append("\n");
            }

            if (result.getEmployees().isEmpty()) {
                sb.append("No employees found in the system.\n");
            }

            reportArea.setText(sb.toString());
        });

        // show total and average salary
        salarySummaryBtn.setOnAction(e -> {
            SearchResult result = employeeService.getAllEmployees(adminUser);
            if (!result.isSuccess()) {
                reportArea.setText("Error: " + result.getMessage());
                return;
            }

            BigDecimal total = BigDecimal.ZERO;
            int count = 0;

            for (Employee emp : result.getEmployees()) {
                if (emp.getBaseSalary() != null) {
                    total = total.add(emp.getBaseSalary());
                    count++;
                }
            }

            BigDecimal avg = BigDecimal.ZERO;
            if (count > 0) {
                // small helper average, rounded to 2 decimals
                avg = total.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Salary Summary Report\n\n");
            sb.append("Employees with salary: ").append(count).append("\n");
            sb.append("Total salary: ").append(total).append("\n");
            sb.append("Average salary: ").append(avg).append("\n");

            reportArea.setText(sb.toString());
        });

        // go back to the admin dashboard
        backBtn.setOnAction(e -> {
            stage.close();
            new AdminDashboard().start(new Stage(), adminUser);
        });

        HBox buttonsRow = new HBox(10, allEmployeesBtn, salarySummaryBtn, backBtn);
        buttonsRow.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(
                12,
                title,
                buttonsRow,
                reportArea
        );
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #f7fbff;");

        Scene scene = new Scene(root, 680, 520);
        stage.setTitle("Reports");
        stage.setScene(scene);
        stage.show();
    }
}
