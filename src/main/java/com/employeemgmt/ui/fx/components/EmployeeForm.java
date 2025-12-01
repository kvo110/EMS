package com.employeemgmt.ui.fx.components;

import com.employeemgmt.models.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// Small form used on the right side of the admin CRUD screen.
// Only focuses on the core DB columns.
public class EmployeeForm {

    private final TextField empIdField = new TextField();
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField hireDateField = new TextField();  // yyyy-mm-dd
    private final TextField salaryField = new TextField();
    private final TextField ssnField = new TextField();

    private final GridPane root;

    private Integer loadedEmployeeId = null;

    public EmployeeForm() {
        empIdField.setEditable(false);
        empIdField.setPromptText("(auto)");

        firstNameField.setPromptText("First name");
        lastNameField.setPromptText("Last name");
        emailField.setPromptText("Email");
        hireDateField.setPromptText("YYYY-MM-DD");
        salaryField.setPromptText("Base salary");
        ssnField.setPromptText("SSN");

        root = new GridPane();
        root.setHgap(8);
        root.setVgap(8);
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccd6f6; -fx-border-radius: 4;");

        int row = 0;
        root.add(new Label("Employee ID:"), 0, row);
        root.add(empIdField, 1, row++);

        root.add(new Label("First Name*:"), 0, row);
        root.add(firstNameField, 1, row++);

        root.add(new Label("Last Name*:"), 0, row);
        root.add(lastNameField, 1, row++);

        root.add(new Label("Email*:"), 0, row);
        root.add(emailField, 1, row++);

        root.add(new Label("Hire Date*:"), 0, row);
        root.add(hireDateField, 1, row++);

        root.add(new Label("Salary*:"), 0, row);
        root.add(salaryField, 1, row++);

        root.add(new Label("SSN:"), 0, row);
        root.add(ssnField, 1, row);
    }

    public Node getNode() {
        return root;
    }

    // Build an Employee object from the current text fields.
    public Employee buildEmployeeFromFields() {
        Employee emp = new Employee();

        if (loadedEmployeeId != null) {
            emp.setEmpid(loadedEmployeeId);
        }

        emp.setFirstName(firstNameField.getText().trim());
        emp.setLastName(lastNameField.getText().trim());
        emp.setEmail(emailField.getText().trim());
        emp.setSsn(ssnField.getText().trim());

        // Hire date
        String hireText = hireDateField.getText().trim();
        if (!hireText.isEmpty()) {
            try {
                emp.setHireDate(LocalDate.parse(hireText));
            } catch (DateTimeParseException ex) {
                emp.setHireDate(null);
            }
        }

        // Salary
        String salaryText = salaryField.getText().trim();
        if (!salaryText.isEmpty()) {
            try {
                emp.setBaseSalary(new BigDecimal(salaryText));
            } catch (NumberFormatException ex) {
                emp.setBaseSalary(null);
            }
        }

        return emp;
    }

    // Load an existing employee into the fields.
    public void loadEmployee(Employee emp) {
        if (emp == null) {
            clear();
            return;
        }

        loadedEmployeeId = emp.getEmpid();
        empIdField.setText(String.valueOf(emp.getEmpid()));
        firstNameField.setText(emp.getFirstName());
        lastNameField.setText(emp.getLastName());
        emailField.setText(emp.getEmail());
        ssnField.setText(emp.getSsn());
        hireDateField.setText(emp.getHireDate() != null ? emp.getHireDate().toString() : "");
        salaryField.setText(emp.getBaseSalary() != null ? emp.getBaseSalary().toPlainString() : "");
    }

    // Clear the form for a new entry.
    public void clear() {
        loadedEmployeeId = null;
        empIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        hireDateField.clear();
        salaryField.clear();
        ssnField.clear();
    }

    public Integer getLoadedEmployeeId() {
        return loadedEmployeeId;
    }
}
