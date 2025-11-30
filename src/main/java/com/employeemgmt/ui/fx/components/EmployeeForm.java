package com.employeemgmt.ui.fx.components;

import com.employeemgmt.models.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/*
   EmployeeForm
   Simple form for editing/creating employees.

   Methods used by ManagementEmployeesScreen:
   - buildEmployeeFromFields()
   - loadEmployee(Employee)
   - clear()
   - getLoadedEmployeeId()
*/
public class EmployeeForm extends VBox {

    private final TextField empIdField = new TextField();
    private final TextField fnameField = new TextField();
    private final TextField lnameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField hireDateField = new TextField();
    private final TextField salaryField = new TextField();
    private final TextField ssnField = new TextField();

    public EmployeeForm() {
        empIdField.setPromptText("Auto-generated");
        empIdField.setEditable(false);

        fnameField.setPromptText("First Name");
        lnameField.setPromptText("Last Name");
        emailField.setPromptText("Email");
        hireDateField.setPromptText("Hire Date (YYYY-MM-DD)");
        salaryField.setPromptText("Base Salary");
        ssnField.setPromptText("SSN");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);

        int row = 0;
        grid.add(new Label("Employee ID:"), 0, row);
        grid.add(empIdField, 1, row++);
        grid.add(new Label("First Name:"), 0, row);
        grid.add(fnameField, 1, row++);
        grid.add(new Label("Last Name:"), 0, row);
        grid.add(lnameField, 1, row++);
        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);
        grid.add(new Label("Hire Date:"), 0, row);
        grid.add(hireDateField, 1, row++);
        grid.add(new Label("Salary:"), 0, row);
        grid.add(salaryField, 1, row++);
        grid.add(new Label("SSN:"), 0, row);
        grid.add(ssnField, 1, row);

        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_LEFT);
        getChildren().add(grid);
    }

    public Employee buildEmployeeFromFields() {
        Employee e = new Employee();

        String first = fnameField.getText().trim();
        String last = lnameField.getText().trim();
        String email = this.emailField.getText().trim();
        String hireText = hireDateField.getText().trim();
        String salaryText = salaryField.getText().trim();
        String ssn = ssnField.getText().trim();

        if (first.isEmpty() || last.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("First name, last name, and email are required.");
        }

        e.setFirstName(first);
        e.setLastName(last);
        e.setEmail(email);

        if (!hireText.isEmpty()) {
            try {
                e.setHireDate(LocalDate.parse(hireText));
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Invalid hire date format. Use YYYY-MM-DD.");
            }
        }

        if (!salaryText.isEmpty()) {
            try {
                e.setBaseSalary(new BigDecimal(salaryText));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Salary must be a valid number.");
            }
        } else {
            e.setBaseSalary(BigDecimal.ZERO);
        }

        if (!ssn.isEmpty()) {
            e.setSsn(ssn);
        }

        String idText = empIdField.getText().trim();
        if (!idText.isEmpty()) {
            try {
                e.setEmpid(Integer.parseInt(idText));
            } catch (NumberFormatException ignored) {
            }
        }

        if (!e.isValid()) {
            throw new IllegalArgumentException("Employee is missing required fields.");
        }

        return e;
    }

    public void loadEmployee(Employee emp) {
        if (emp == null) {
            clear();
            return;
        }

        empIdField.setText(String.valueOf(emp.getEmpid()));
        fnameField.setText(emp.getFirstName());
        lnameField.setText(emp.getLastName());
        emailField.setText(emp.getEmail());

        if (emp.getHireDate() != null) {
            hireDateField.setText(emp.getHireDate().toString());
        } else {
            hireDateField.clear();
        }

        if (emp.getBaseSalary() != null) {
            salaryField.setText(emp.getBaseSalary().toPlainString());
        } else {
            salaryField.clear();
        }

        ssnField.setText(emp.getSsn() != null ? emp.getSsn() : "");
    }

    public void clear() {
        empIdField.clear();
        fnameField.clear();
        lnameField.clear();
        emailField.clear();
        hireDateField.clear();
        salaryField.clear();
        ssnField.clear();
    }

    public Integer getLoadedEmployeeId() {
        String idText = empIdField.getText().trim();
        if (idText.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}