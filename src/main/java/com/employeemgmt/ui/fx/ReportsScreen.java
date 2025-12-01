package com.employeemgmt.ui.fx;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.math.BigDecimal;

/*
    ReportsScreen
    -------------
    Very lightweight reporting page.

    Right now:
    - "All Employees" report (simple dump)
    - "Salary Summary" report with total + average salary
*/
public class ReportsScreen {

    private final EmployeeService employeeService = new EmployeeService();

    public void start(Stage stage, User adminUser) {
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

        allEmployeesBtn.setOnAction(e -> {
            SearchResult result = employeeService.getAllEmployees(adminUser);
            if (!result.isSuccess()) {
                reportArea.setText("Error: " + result.getMessage());
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("All Employees Report\n");
            sb.append("====================\n\n");
            for (Employee emp : result.getEmployees()) {
                sb.append("ID: ").append(emp.getEmpid()).append("  ");
                sb.append("Name: ").append(emp.getFullName()).append("  ");
                sb.append("Email: ").append(emp.getEmail()).append("  ");
                sb.append("Salary: ").append(emp.getFormattedSalary()).append("\n");
            }
            reportArea.setText(sb.toString());
        });

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
                avg = total.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Salary Summary Report\n");
            sb.append("=====================\n\n");
            sb.append("Total employees with salary: ").append(count).append("\n");
            sb.append("Total salary: ").append(total).append("\n");
            sb.append("Average salary: ").append(avg).append("\n");

            reportArea.setText(sb.toString());
        });

        backBtn.setOnAction(e -> {
            stage.close();
            new AdminDashboard().start(new Stage(), adminUser);
        });

        HBox buttonsRow = new HBox(10, allEmployeesBtn, salarySummaryBtn, backBtn);

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
