package com.employeemgmt.ui.fx.components;

import com.employeemgmt.models.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
    EmployeeForm
    ------------
    Right-hand form for editing / creating employee records.

    Only covers actual DB columns:
    - first name, last name, email, hire date, salary, SSN
*/
public class EmployeeForm extends VBox {

    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField emailField = new TextField();
    private final DatePicker hireDatePicker = new DatePicker();
    private final TextField salaryField = new TextField();
    private final TextField ssnField = new TextField();

    // store current employee (id)
    private Employee currentEmployee;

    public EmployeeForm() {
        firstNameField.setPromptText("First name");
        lastNameField.setPromptText("Last name");
        emailField.setPromptText("Email");
        salaryField.setPromptText("Base salary (e.g. 55000)");
        ssnField.setPromptText("SSN (optional)");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        int row = 0;
        grid.add(new Label("First Name:"), 0, row);
        grid.add(firstNameField, 1, row++);

        grid.add(new Label("Last Name:"), 0, row);
        grid.add(lastNameField, 1, row++);

        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);

        grid.add(new Label("Hire Date:"), 0, row);
        grid.add(hireDatePicker, 1, row++);

        grid.add(new Label("Salary:"), 0, row);
        grid.add(salaryField, 1, row++);

        grid.add(new Label("SSN:"), 0, row);
        grid.add(ssnField, 1, row++);

        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_LEFT);
        getChildren().add(grid);
    }

    // builds an Employee object from the current form values
    public Employee buildEmployeeFromForm() {
        Employee e = (currentEmployee != null) ? currentEmployee : new Employee();

        e.setFirstName(firstNameField.getText().trim());
        e.setLastName(lastNameField.getText().trim());
        e.setEmail(emailField.getText().trim());
        e.setHireDate(hireDatePicker.getValue());

        String salaryText = salaryField.getText().trim();
        if (!salaryText.isEmpty()) {
            try {
                BigDecimal salary = new BigDecimal(salaryText);
                e.setBaseSalary(salary);
            } catch (NumberFormatException ex) {
                // quick fallback if user types something weird
                e.setBaseSalary(BigDecimal.ZERO);
            }
        } else {
            e.setBaseSalary(BigDecimal.ZERO);
        }

        e.setSsn(ssnField.getText().trim().isEmpty() ? null : ssnField.getText().trim());
        return e;
    }

    // load an existing employee into the form
    public void loadEmployee(Employee emp) {
        if (emp == null) {
            clearForm();
            return;
        }
        this.currentEmployee = emp;

        firstNameField.setText(emp.getFirstName());
        lastNameField.setText(emp.getLastName());
        emailField.setText(emp.getEmail());
        hireDatePicker.setValue(emp.getHireDate());
        salaryField.setText(emp.getBaseSalary() != null ? emp.getBaseSalary().toPlainString() : "");
        ssnField.setText(emp.getSsn() != null ? emp.getSsn() : "");
    }

    public void clearForm() {
        currentEmployee = null;
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        hireDatePicker.setValue(null);
        salaryField.clear();
        ssnField.clear();
    }

    public Integer getCurrentEmployeeId() {
        if (currentEmployee == null) return null;
        return currentEmployee.getEmpid();
    }
}
