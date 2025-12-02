package com.employeemgmt.ui.fx;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;
import com.employeemgmt.dao.DatabaseConnection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
    ReportsScreen
    -------------
    Admin-only reporting screen.

    Features now:
    - All Employees report
    - Salary summary (total + average base salary)
    - Total pay for a given month by job title
    - Total pay for a given month by division
    - Employees hired within a given date range
*/
public class ReportsScreen {

    private final EmployeeService employeeService = new EmployeeService();
    private final DatabaseConnection db = DatabaseConnection.getInstance();

    public void start(Stage stage, User adminUser) {
        // only admins should be able to open this
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

        // top-level buttons
        Button allEmployeesBtn = new Button("All Employees");
        Button salarySummaryBtn = new Button("Salary Summary");
        Button backBtn = new Button("Back to Admin");

        // month/year inputs for pay-by-job-title and pay-by-division
        TextField monthField = new TextField();
        monthField.setPromptText("Month (1-12)");
        monthField.setPrefWidth(90);

        TextField yearField = new TextField();
        yearField.setPromptText("Year (e.g. 2025)");
        yearField.setPrefWidth(110);

        Button payByJobTitleBtn = new Button("Pay by Job Title");
        Button payByDivisionBtn = new Button("Pay by Division");

        // date pickers for hires-in-range report
        DatePicker fromPicker = new DatePicker();
        fromPicker.setPromptText("From date");

        DatePicker toPicker = new DatePicker();
        toPicker.setPromptText("To date");

        Button hiresRangeBtn = new Button("Hires in Range");

        // keep button sizes similar
        for (Button b : new Button[]{allEmployeesBtn, salarySummaryBtn, backBtn,
                                     payByJobTitleBtn, payByDivisionBtn, hiresRangeBtn}) {
            b.setStyle("-fx-font-size: 12px;");
        }

        // === Existing: All Employees report ===
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

        // === Existing: Salary summary ===
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

        // === New: monthly total pay by job title ===
        payByJobTitleBtn.setOnAction(e -> {
            reportArea.clear();

            int month, year;
            try {
                month = Integer.parseInt(monthField.getText().trim());
                year = Integer.parseInt(yearField.getText().trim());
            } catch (NumberFormatException ex) {
                reportArea.setText("Please enter a valid month (1-12) and year (e.g. 2025).");
                return;
            }

            String sql = """
                SELECT jt.title AS job_title,
                       SUM(ps.gross_pay) AS total_pay
                FROM pay_statements ps
                JOIN employee_job ej ON ps.empid = ej.empid
                JOIN job_title jt ON ej.job_id = jt.job_id
                WHERE MONTH(ps.pay_date) = ? AND YEAR(ps.pay_date) = ?
                GROUP BY jt.title
                ORDER BY jt.title
            """;

            StringBuilder sb = new StringBuilder();
            sb.append("Total Pay by Job Title for ").append(month).append("/").append(year).append("\n");
            sb.append("===========================================\n");

            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, month);
                ps.setInt(2, year);
                ResultSet rs = ps.executeQuery();

                boolean any = false;
                while (rs.next()) {
                    any = true;
                    String jobTitle = rs.getString("job_title");
                    BigDecimal totalPay = rs.getBigDecimal("total_pay");
                    sb.append(jobTitle)
                      .append(" : ")
                      .append(String.format("$%,.2f", totalPay))
                      .append("\n");
                }

                if (!any) {
                    sb.append("No pay data found for this month.\n");
                }

            } catch (SQLException ex) {
                sb.append("DB error: ").append(ex.getMessage()).append("\n");
            }

            reportArea.setText(sb.toString());
        });

        // === New: monthly total pay by division ===
        payByDivisionBtn.setOnAction(e -> {
            reportArea.clear();

            int month, year;
            try {
                month = Integer.parseInt(monthField.getText().trim());
                year = Integer.parseInt(yearField.getText().trim());
            } catch (NumberFormatException ex) {
                reportArea.setText("Please enter a valid month (1-12) and year (e.g. 2025).");
                return;
            }

            String sql = """
                SELECT d.Name AS division_name,
                       SUM(ps.gross_pay) AS total_pay
                FROM pay_statements ps
                JOIN employee_division ed ON ps.empid = ed.empid
                JOIN division d ON ed.div_ID = d.ID
                WHERE MONTH(ps.pay_date) = ? AND YEAR(ps.pay_date) = ?
                GROUP BY d.Name
                ORDER BY d.Name
            """;

            StringBuilder sb = new StringBuilder();
            sb.append("Total Pay by Division for ").append(month).append("/").append(year).append("\n");
            sb.append("========================================\n");

            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, month);
                ps.setInt(2, year);
                ResultSet rs = ps.executeQuery();

                boolean any = false;
                while (rs.next()) {
                    any = true;
                    String divName = rs.getString("division_name");
                    BigDecimal totalPay = rs.getBigDecimal("total_pay");
                    sb.append(divName)
                      .append(" : ")
                      .append(String.format("$%,.2f", totalPay))
                      .append("\n");
                }

                if (!any) {
                    sb.append("No pay data found for this month.\n");
                }

            } catch (SQLException ex) {
                sb.append("DB error: ").append(ex.getMessage()).append("\n");
            }

            reportArea.setText(sb.toString());
        });

        // === New: employees hired within a date range ===
        hiresRangeBtn.setOnAction(e -> {
            reportArea.clear();

            if (fromPicker.getValue() == null || toPicker.getValue() == null) {
                reportArea.setText("Please pick both From and To dates.");
                return;
            }

            java.time.LocalDate from = fromPicker.getValue();
            java.time.LocalDate to = toPicker.getValue();

            String sql = """
                SELECT empid, Fname, Lname, HireDate
                FROM employees
                WHERE HireDate BETWEEN ? AND ?
                ORDER BY HireDate
            """;

            StringBuilder sb = new StringBuilder();
            sb.append("Employees hired between ")
              .append(from).append(" and ").append(to).append("\n");
            sb.append("======================================\n");

            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setDate(1, java.sql.Date.valueOf(from));
                ps.setDate(2, java.sql.Date.valueOf(to));
                ResultSet rs = ps.executeQuery();

                boolean any = false;
                while (rs.next()) {
                    any = true;
                    int empid = rs.getInt("empid");
                    String fname = rs.getString("Fname");
                    String lname = rs.getString("Lname");
                    java.sql.Date d = rs.getDate("HireDate");

                    sb.append("ID: ").append(empid)
                      .append("  Name: ").append(fname).append(" ").append(lname)
                      .append("  Hire Date: ").append(d)
                      .append("\n");
                }

                if (!any) {
                    sb.append("No employees hired in this range.\n");
                }

            } catch (SQLException ex) {
                sb.append("DB error: ").append(ex.getMessage()).append("\n");
            }

            reportArea.setText(sb.toString());
        });

        backBtn.setOnAction(e -> {
            stage.close();
            new AdminDashboard().start(new Stage(), adminUser);
        });

        // layout for the original buttons
        HBox topButtons = new HBox(10, allEmployeesBtn, salarySummaryBtn, backBtn);
        topButtons.setAlignment(Pos.CENTER_LEFT);

        // layout for month/year row
        HBox monthYearRow = new HBox(
                8,
                new Label("Month:"), monthField,
                new Label("Year:"), yearField,
                payByJobTitleBtn,
                payByDivisionBtn
        );
        monthYearRow.setAlignment(Pos.CENTER_LEFT);

        // layout for hires range row
        HBox hiresRow = new HBox(
                8,
                new Label("From:"), fromPicker,
                new Label("To:"), toPicker,
                hiresRangeBtn
        );
        hiresRow.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(
                10,
                title,
                topButtons,
                new Label("Monthly pay reports (job title / division):"),
                monthYearRow,
                new Label("Employees hired within a date range:"),
                hiresRow,
                reportArea
        );
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #f7fbff;");

        Scene scene = new Scene(root, 780, 600);
        stage.setTitle("Reports");
        stage.setScene(scene);
        stage.show();
    }
}
