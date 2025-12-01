package com.employeemgmt.ui.fx.components;

import com.employeemgmt.models.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/*
 * EmployeeForm
 * ------------
 * Simple form for editing one employee.
 * Fields:
 * - First name, last name, email
 * - Hire date (yyyy-mm-dd)
 * - Salary
 * - SSN (optional)
 */
public class EmployeeForm {

    private final VBox root;
    private final TextField fnameField;
    private final TextField lnameField;
    private final TextField emailField;
    private final TextField hireDateField;
    private final TextField salaryField;
    private final TextField ssnField;

    // keep track of which employee is currently loaded
    private Integer loadedEmployeeId;

    public EmployeeForm() {
        fnameField = new TextField();
        lnameField = new TextField();
        emailField = new TextField();
        hireDateField = new TextField();
        salaryField = new TextField();
        ssnField = new TextField();

        fnameField.setPromptText("First name");
        lnameField.setPromptText("Last name");
        emailField.setPromptText("Email");
        hireDateField.setPromptText("Hire date (yyyy-mm-dd)");
        salaryField.setPromptText("Salary, e.g. 55000");
        ssnField.setPromptText("SSN (optional)");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(fnameField, 1, 0);

        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lnameField, 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        grid.add(new Label("Hire Date:"), 0, 3);
        grid.add(hireDateField, 1, 3);

        grid.add(new Label("Salary:"), 0, 4);
        grid.add(salaryField, 1, 4);

        grid.add(new Label("SSN:"), 0, 5);
        grid.add(ssnField, 1, 5);

        root = new VBox(10, grid);
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(10));
    }

    // builds a new Employee object from the fields
    public Employee buildEmployeeFromFields() {
        Employee e = new Employee();

        if (loadedEmployeeId != null) {
            e.setEmpid(loadedEmployeeId);
        }

        e.setFirstName(fnameField.getText().trim());
        e.setLastName(lnameField.getText().trim());
        e.setEmail(emailField.getText().trim());

        // hire date parsing
        String hd = hireDateField.getText().trim();
        if (!hd.isEmpty()) {
            try {
                e.setHireDate(LocalDate.parse(hd));
            } catch (DateTimeParseException ex) {
                // if parsing fails we just leave it null
                e.setHireDate(null);
            }
        }

        String salaryText = salaryField.getText().trim();
        if (!salaryText.isEmpty()) {
            try {
                double val = Double.parseDouble(salaryText);
                e.setBaseSalary(BigDecimal.valueOf(val));
            } catch (NumberFormatException ex) {
                e.setBaseSalary(null);
            }
        }

        e.setSsn(ssnField.getText().trim().isEmpty() ? null : ssnField.getText().trim());

        return e;
    }

    public void loadEmployee(Employee emp) {
        if (emp == null) {
            clear();
            return;
        }

        loadedEmployeeId = emp.getEmpid();

        fnameField.setText(emp.getFirstName());
        lnameField.setText(emp.getLastName());
        emailField.setText(emp.getEmail());
        hireDateField.setText(emp.getHireDate() != null ? emp.getHireDate().toString() : "");
        salaryField.setText(emp.getBaseSalary() != null ? emp.getBaseSalary().toPlainString() : "");
        ssnField.setText(emp.getSsn() != null ? emp.getSsn() : "");
    }

    public void clear() {
        loadedEmployeeId = null;
        fnameField.clear();
        lnameField.clear();
        emailField.clear();
        hireDateField.clear();
        salaryField.clear();
        ssnField.clear();
    }

    public Node getNode() {
        return root;
    }

    public Integer getLoadedEmployeeId() {
        return loadedEmployeeId;
    }
}
