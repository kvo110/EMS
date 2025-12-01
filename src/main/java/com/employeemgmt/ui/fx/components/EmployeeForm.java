package com.employeemgmt.ui.fx.components;

import com.employeemgmt.models.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;

// small form for editing employee info
public class EmployeeForm {

    private final TextField fnameField = new TextField();
    private final TextField lnameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField ssnField = new TextField();
    private final DatePicker hirePicker = new DatePicker();
    private final TextField salaryField = new TextField();

    private Integer loadedEmpId = null;

    private final VBox root;

    public EmployeeForm() {

        fnameField.setPromptText("First Name");
        lnameField.setPromptText("Last Name");
        emailField.setPromptText("Email");
        ssnField.setPromptText("SSN");
        salaryField.setPromptText("Salary (number)");
        hirePicker.setPromptText("Hire Date");

        root = new VBox(8,
                fnameField,
                lnameField,
                emailField,
                ssnField,
                hirePicker,
                salaryField
        );
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);
    }

    public Node getNode() {
        return root;
    }

    // grab id if editing existing employee
    public Integer getLoadedEmployeeId() {
        return loadedEmpId;
    }

    // turn UI fields into Employee
    public Employee buildEmployeeFromFields() {
        try {
            Employee e = new Employee();
            if (loadedEmpId != null) e.setEmpid(loadedEmpId);

            e.setFirstName(fnameField.getText().trim());
            e.setLastName(lnameField.getText().trim());
            e.setEmail(emailField.getText().trim());
            e.setSsn(ssnField.getText().trim());

            if (hirePicker.getValue() != null)
                e.setHireDate(hirePicker.getValue());

            if (!salaryField.getText().isBlank())
                e.setBaseSalary(new BigDecimal(salaryField.getText().trim()));
            else
                e.setBaseSalary(BigDecimal.ZERO);

            return e;

        } catch (Exception ex) {
            return null;
        }
    }

    // load selected row into form
    public void loadEmployee(Employee emp) {
        loadedEmpId = emp.getEmpid();

        fnameField.setText(emp.getFirstName());
        lnameField.setText(emp.getLastName());
        emailField.setText(emp.getEmail());
        ssnField.setText(emp.getSsn());
        hirePicker.setValue(emp.getHireDate());
        salaryField.setText(
                emp.getBaseSalary() == null ? "" : emp.getBaseSalary().toString()
        );
    }

    // clear form
    public void clear() {
        loadedEmpId = null;
        fnameField.clear();
        lnameField.clear();
        emailField.clear();
        ssnField.clear();
        hirePicker.setValue(null);
        salaryField.clear();
    }
}
