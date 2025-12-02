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
import java.time.LocalDate;

/*
    ReportsScreen
    -------------
    Light reporting page for HR admins.

    Currently:
    - All Employees report
    - Salary Summary (total + average)
    - Employees hired in a date range
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
        reportArea.setPrefRowCount(18);

        Button allEmployeesBtn = new Button("All Employees");
        Button salarySummaryBtn = new Button("Salary Summary");
        Button hiredRangeBtn = new Button("Hired in Date Range");
        Button backBtn = new Button("Back to Admin");

        // date pickers for hire range report
        DatePicker fromPicker = new DatePicker();
        fromPicker.setPromptText("Start date");
        DatePicker toPicker = new DatePicker();
        toPicker.setPromptText("End date");

        HBox rangeControls = new HBox(
                8,
                new Label("From:"), fromPicker,
                new Label("To:"), toPicker,
                hiredRangeBtn
        );
        rangeControls.setAlignment(Pos.CENTER_LEFT);

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

        hiredRangeBtn.setOnAction(e -> {
            LocalDate from = fromPicker.getValue();
            LocalDate to = toPicker.getValue();

            SearchResult result = employeeService.getEmployeesHiredBetween(from, to, adminUser);
            if (!result.isSuccess()) {
                reportArea.setText("Error: " + result.getMessage());
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Employees Hired Between ")
              .append(from).append(" and ").append(to).append("\n");
            sb.append("====================================\n\n");

            for (Employee emp : result.getEmployees()) {
                sb.append("ID: ").append(emp.getEmpid()).append("  ");
                sb.append("Name: ").append(emp.getFullName()).append("  ");
                sb.append("Hire Date: ").append(emp.getHireDate()).append("\n");
            }

            if (result.getEmployees().isEmpty()) {
                sb.append("No employees hired in this range.\n");
            }

            reportArea.setText(sb.toString());
        });

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
                rangeControls,
                reportArea
        );
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #f7fbff;");

        Scene scene = new Scene(root, 720, 540);
        stage.setTitle("Reports");
        stage.setScene(scene);
        stage.show();
    }
}
