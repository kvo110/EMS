package com.employeemgmt.ui.fx;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;
import com.employeemgmt.services.ReportService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/*
    ReportsScreen
    -------------
    Basic reporting screen for HR admins.

    Currently supports:
    - All employees dump
    - Salary summary (total + average)
    - Total pay for month by job title   (uses pay_statements + job_title)
    - Total pay for month by division    (uses pay_statements + division)
    - Employees hired in a date range    (HireDate from employees)
*/
public class ReportsScreen {

    private final EmployeeService employeeService = new EmployeeService();
    private final ReportService reportService = new ReportService();

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

        // existing buttons
        Button allEmployeesBtn = new Button("All Employees");
        Button salarySummaryBtn = new Button("Salary Summary");

        // new report inputs
        TextField yearField = new TextField();
        yearField.setPromptText("Year (e.g. 2025)");
        yearField.setPrefWidth(110);

        TextField monthField = new TextField();
        monthField.setPromptText("Month (1-12)");
        monthField.setPrefWidth(100);

        Button jobPayBtn = new Button("Monthly Job Pay");
        Button divisionPayBtn = new Button("Monthly Division Pay");

        DatePicker startHirePicker = new DatePicker();
        startHirePicker.setPromptText("Hire start");

        DatePicker endHirePicker = new DatePicker();
        endHirePicker.setPromptText("Hire end");

        Button hiresRangeBtn = new Button("Hires in Range");

        Button backBtn = new Button("Back to Admin");

        // --- existing behavior ---

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

        // --- new reports ---

        jobPayBtn.setOnAction(e -> {
            try {
                int year = Integer.parseInt(yearField.getText().trim());
                int month = Integer.parseInt(monthField.getText().trim());
                List<String> lines = reportService.monthlyPayByJob(year, month, adminUser);
                reportArea.setText(String.join("\n", lines));
            } catch (NumberFormatException ex) {
                reportArea.setText("Please enter a valid year and month.");
            }
        });

        divisionPayBtn.setOnAction(e -> {
            try {
                int year = Integer.parseInt(yearField.getText().trim());
                int month = Integer.parseInt(monthField.getText().trim());
                List<String> lines = reportService.monthlyPayByDivision(year, month, adminUser);
                reportArea.setText(String.join("\n", lines));
            } catch (NumberFormatException ex) {
                reportArea.setText("Please enter a valid year and month.");
            }
        });

        hiresRangeBtn.setOnAction(e -> {
            LocalDate startDate = startHirePicker.getValue();
            LocalDate endDate = endHirePicker.getValue();

            if (startDate == null || endDate == null) {
                reportArea.setText("Please pick both start and end dates.");
                return;
            }

            if (endDate.isBefore(startDate)) {
                reportArea.setText("End date cannot be before start date.");
                return;
            }

            List<String> lines = reportService.employeesHiredBetween(startDate, endDate, adminUser);
            reportArea.setText(String.join("\n", lines));
        });

        backBtn.setOnAction(e -> {
            stage.close();
            new AdminDashboard().start(new Stage(), adminUser);
        });

        HBox topRow = new HBox(10, allEmployeesBtn, salarySummaryBtn);
        HBox monthRow = new HBox(8, yearField, monthField, jobPayBtn, divisionPayBtn);
        HBox hireRow = new HBox(8, startHirePicker, endHirePicker, hiresRangeBtn);

        VBox root = new VBox(
                10,
                title,
                topRow,
                monthRow,
                hireRow,
                reportArea,
                backBtn
        );
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #f7fbff;");

        Scene scene = new Scene(root, 780, 560);
        stage.setTitle("Reports");
        stage.setScene(scene);
        stage.show();
    }
}
