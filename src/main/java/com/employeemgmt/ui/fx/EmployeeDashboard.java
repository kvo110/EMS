package com.employeemgmt.ui.fx;

import com.employeemgmt.dao.DatabaseConnection;
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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
    EmployeeDashboard
    -----------------
    Home screen for a regular employee.

    Now it does two things:
    - Shows basic profile info (from employees table)
    - Lets the employee view their pay history from pay_statements
*/
public class EmployeeDashboard {

    private final EmployeeService employeeService = new EmployeeService();
    private final DatabaseConnection db = DatabaseConnection.getInstance();

    public void start(Stage stage, User user) {
        // only non-admin accounts should land here
        if (user == null || user.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label title = new Label("Welcome, " + user.getUsername());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // basic profile info box
        TextArea profileArea = new TextArea();
        profileArea.setEditable(false);
        profileArea.setPrefRowCount(8);

        // pay history area (this is the new part)
        Label payLabel = new Label("My Pay Statement History (most recent first)");
        TextArea payHistoryArea = new TextArea();
        payHistoryArea.setEditable(false);
        payHistoryArea.setPrefRowCount(10);

        Button refreshProfileBtn = new Button("Refresh My Profile");
        Button loadPayHistoryBtn = new Button("View My Pay History");
        Button logoutBtn = new Button("Logout");

        // load the employee's profile from EmployeeService
        refreshProfileBtn.setOnAction(e -> {
            SearchResult result = employeeService.searchEmployeeById(user.getEmpid(), user);
            if (!result.isSuccess() || result.getEmployees().isEmpty()) {
                profileArea.setText("Could not load your profile.\n" + result.getMessage());
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

        // load pay history from pay_statements for the logged-in empid
        loadPayHistoryBtn.setOnAction(e -> {
            String text = buildPayHistoryText(user.getEmpid());
            payHistoryArea.setText(text);
        });

        logoutBtn.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox layout = new VBox(
                10,
                title,
                profileArea,
                refreshProfileBtn,
                payLabel,
                payHistoryArea,
                loadPayHistoryBtn,
                logoutBtn
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #f5f9ff;");

        Scene scene = new Scene(layout, 650, 600);
        stage.setTitle("Employee Dashboard");
        stage.setScene(scene);
        stage.show();

        // show profile by default when the screen opens
        refreshProfileBtn.fire();
    }

    // helper to build the pay history text from pay_statements
    private String buildPayHistoryText(int empid) {
        String sql = """
            SELECT pay_date, gross_pay, net_pay
            FROM pay_statements
            WHERE empid = ?
            ORDER BY pay_date DESC
        """;

        StringBuilder sb = new StringBuilder();
        sb.append("Date        | Gross Pay | Net Pay\n");
        sb.append("---------------------------------\n");

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, empid);
            ResultSet rs = ps.executeQuery();

            boolean any = false;

            while (rs.next()) {
                any = true;
                java.sql.Date d = rs.getDate("pay_date");
                BigDecimal gross = rs.getBigDecimal("gross_pay");
                BigDecimal net = rs.getBigDecimal("net_pay");

                sb.append(d).append(" | ")
                  .append(String.format("$%,.2f", gross)).append(" | ")
                  .append(String.format("$%,.2f", net)).append("\n");
            }

            if (!any) {
                sb.append("(No pay statements found for your account yet.)\n");
            }

        } catch (SQLException ex) {
            sb.append("Error loading pay history: ").append(ex.getMessage()).append("\n");
        }

        return sb.toString();
    }
}
