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
    SalaryToolsScreen
    -----------------
    Small helper screen for HR to adjust salaries.

    Two ideas:
    1) Raise a single employee by X%
    2) Raise ALL employees in a salary range by X%
*/
public class SalaryToolsScreen {

    private final EmployeeService employeeService = new EmployeeService();

    public void start(Stage stage, User adminUser) {
        if (adminUser == null || !adminUser.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label title = new Label("Salary Tools");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setPrefRowCount(10);

        // single employee section
        Label singleLabel = new Label("Single Employee Raise");
        TextField empIdField = new TextField();
        empIdField.setPromptText("Employee ID");
        TextField percentSingleField = new TextField();
        percentSingleField.setPromptText("Raise % (e.g. 5)");
        Button applySingleBtn = new Button("Apply to Employee");

        applySingleBtn.setOnAction(e -> {
            output.clear();
            try {
                int empid = Integer.parseInt(empIdField.getText().trim());
                double percent = Double.parseDouble(percentSingleField.getText().trim());

                SearchResult sr = employeeService.searchEmployeeById(empid, adminUser);
                if (!sr.isSuccess() || sr.getEmployees().isEmpty()) {
                    output.setText("Could not find employee: " + sr.getMessage());
                    return;
                }

                Employee emp = sr.getEmployees().get(0);
                BigDecimal oldSalary = emp.getBaseSalary();
                if (oldSalary == null) {
                    output.setText("Employee has no base salary set.");
                    return;
                }

                BigDecimal multiplier = BigDecimal.valueOf(1 + percent / 100.0);
                BigDecimal newSalary = oldSalary.multiply(multiplier);
                emp.setBaseSalary(newSalary);

                SearchResult updateResult = employeeService.updateEmployee(emp, adminUser);
                if (updateResult.isSuccess()) {
                    output.setText("Updated salary for " + emp.getFullName() +
                            " from " + oldSalary + " to " + newSalary);
                } else {
                    output.setText("Update failed: " + updateResult.getMessage());
                }

            } catch (NumberFormatException ex) {
                output.setText("Please enter valid numeric values for employee ID and percentage.");
            }
        });

        // range / all employees section
        Label rangeLabel = new Label("Raise by Salary Range (can act like 'all')");
        TextField percentRangeField = new TextField();
        percentRangeField.setPromptText("Raise % for range (e.g. 3)");
        TextField minField = new TextField();
        minField.setPromptText("Min salary (e.g. 0)");
        TextField maxField = new TextField();
        maxField.setPromptText("Max salary (e.g. 999999)");
        Button applyRangeBtn = new Button("Apply to Range");

        applyRangeBtn.setOnAction(e -> {
            output.clear();
            try {
                double percent = Double.parseDouble(percentRangeField.getText().trim());
                double min = Double.parseDouble(minField.getText().trim());
                double max = Double.parseDouble(maxField.getText().trim());

                SearchResult sr = employeeService.updateSalaryRange(percent, min, max, adminUser);
                if (sr.isSuccess()) {
                    output.setText("Range update complete: " + sr.getMessage());
                } else {
                    output.setText("Range update failed: " + sr.getMessage());
                }

            } catch (NumberFormatException ex) {
                output.setText("Please enter valid numeric values for percentage and range.");
            }
        });

        Button backBtn = new Button("Back to Admin");
        backBtn.setOnAction(e -> {
            stage.close();
            new AdminDashboard().start(new Stage(), adminUser);
        });

        VBox singleBox = new VBox(
                6,
                singleLabel,
                new HBox(8, new Label("Emp ID:"), empIdField),
                new HBox(8, new Label("Raise %:"), percentSingleField),
                applySingleBtn
        );

        VBox rangeBox = new VBox(
                6,
                rangeLabel,
                new HBox(8, new Label("Raise %:"), percentRangeField),
                new HBox(8, new Label("Min:"), minField),
                new HBox(8, new Label("Max:"), maxField),
                applyRangeBtn
        );

        VBox root = new VBox(
                12,
                title,
                singleBox,
                rangeBox,
                new Label("Log:"),
                output,
                backBtn
        );
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #f7fbff;");
        VBox.setMargin(backBtn, new Insets(8, 0, 0, 0));

        Scene scene = new Scene(root, 640, 520);
        stage.setTitle("Salary Tools");
        stage.setScene(scene);
        stage.show();
    }
}
