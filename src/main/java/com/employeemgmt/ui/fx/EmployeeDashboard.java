package com.employeemgmt.ui.fx;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.PayStatement;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;
import com.employeemgmt.dao.PayStatementDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/*
    EmployeeDashboard
    -----------------
    Home screen for a regular employee.

    Now supports:
    - viewing their profile info
    - viewing their own pay statement history (most recent first)
    - logging out
*/
public class EmployeeDashboard {

    private final EmployeeService employeeService = new EmployeeService();
    private final PayStatementDAO payDao = new PayStatementDAO();

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
        profileArea.setPrefRowCount(6);

        TextArea payArea = new TextArea();
        payArea.setEditable(false);
        payArea.setPrefRowCount(10);

        Button refreshProfileBtn = new Button("Refresh My Profile");
        Button viewPayHistoryBtn = new Button("View My Pay History");
        Button logoutBtn = new Button("Logout");

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

        // Shows pay_statements for that employee, newest pay_date first
        viewPayHistoryBtn.setOnAction(e -> {
            List<PayStatement> list = payDao.findByEmployeeOrdered(user.getEmpid());

            if (list.isEmpty()) {
                payArea.setText("No pay statements found for your account yet.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Pay History (most recent first)\n");
            sb.append("================================\n\n");

            for (PayStatement p : list) {
                sb.append("Date: ").append(p.getPayDate()).append("\n");
                sb.append("Gross: ").append(p.getGrossPay()).append("\n");
                sb.append("Net: ").append(p.getNetPay()).append("\n");
                sb.append("------------------------------\n");
            }

            payArea.setText(sb.toString());
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
                viewPayHistoryBtn,
                payArea,
                logoutBtn
        );
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #f5f9ff;");

        Scene scene = new Scene(layout, 600, 550);
        stage.setTitle("Employee Dashboard");
        stage.setScene(scene);
        stage.show();

        // automatically load profile info on open
        refreshProfileBtn.fire();
    }
}
