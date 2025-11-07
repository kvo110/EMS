package com.employeemgmt.ui;

import com.employeemgmt.dao.EmployeeDAO;
import com.employeemgmt.models.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/*
   Screen for HR Admin to manage employee records.
   This version can be opened directly from AdminDashboard with a username.
*/
public class ManageEmployeesScreen {

    private TableView<Employee> table;
    private ObservableList<Employee> employees;
    private EmployeeDAO dao;

    // Added this so AdminDashboard can pass the username when opening this screen
    public void start(Stage stage, String username) {
        setup(stage);
    }

    // This method builds and shows the Manage Employees window
    private void setup(Stage stage) {
        stage.setTitle("Manage Employees");

        // initialize DAO and table data
        dao = new EmployeeDAO();
        employees = FXCollections.observableArrayList();

        // layout for everything
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // main employee table
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // first name column
        TableColumn<Employee, String> fnameCol = new TableColumn<>("First Name");
        fnameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));

        // last name column
        TableColumn<Employee, String> lnameCol = new TableColumn<>("Last Name");
        lnameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));

        // salary column
        TableColumn<Employee, String> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getBaseSalary() != null
                                ? "$" + data.getValue().getBaseSalary().toString()
                                : "N/A"
                )
        );

        table.getColumns().addAll(fnameCol, lnameCol, salaryCol);

        // load employee data
        refreshEmployeeTable();

        // buttons for add, edit, and refresh
        Button addBtn = new Button("Add Employee");
        Button editBtn = new Button("Edit Selected");
        Button refreshBtn = new Button("Refresh");
        Button backBtn = new Button("Back");

        addBtn.setOnAction(e -> showAddEmployeeDialog());
        editBtn.setOnAction(e -> {
            Employee selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditEmployeeDialog(selected);
            } else {
                showAlert("No employee selected", "Please select an employee to edit.");
            }
        });
        refreshBtn.setOnAction(e -> refreshEmployeeTable());
        backBtn.setOnAction(e -> {
            stage.close();
            new AdminDashboard().start(new Stage(), "Admin");
        });

        HBox buttonBox = new HBox(10, addBtn, editBtn, refreshBtn, backBtn);
        buttonBox.setPadding(new Insets(10));

        root.setCenter(table);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.show();
    }

    // reloads all employees from the database
    private void refreshEmployeeTable() {
        try {
            List<Employee> list = dao.findAll();
            employees.setAll(list);
            table.setItems(employees);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load employees: " + e.getMessage());
        }
    }

    // simple window for adding an employee
    private void showAddEmployeeDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Employee");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        TextField fnameField = new TextField();
        fnameField.setPromptText("First Name");

        TextField lnameField = new TextField();
        lnameField.setPromptText("Last Name");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Base Salary");

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                Employee emp = new Employee();
                emp.setFirstName(fnameField.getText());
                emp.setLastName(lnameField.getText());
                emp.setHireDate(LocalDate.now());
                emp.setBaseSalary(BigDecimal.valueOf(Double.parseDouble(salaryField.getText())));
                emp.setSsn("000-00-0000");

                dao.save(emp);
                refreshEmployeeTable();
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", "Could not add employee: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(
                new Label("First Name:"), fnameField,
                new Label("Last Name:"), lnameField,
                new Label("Salary:"), salaryField, saveBtn
        );

        Scene scene = new Scene(layout, 300, 250);
        dialog.setScene(scene);
        dialog.show();
    }

    // simple window for editing selected employee
    private void showEditEmployeeDialog(Employee selected) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit Employee");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        TextField fnameField = new TextField(selected.getFirstName());
        TextField lnameField = new TextField(selected.getLastName());
        TextField salaryField = new TextField(
                selected.getBaseSalary() != null ? selected.getBaseSalary().toString() : ""
        );

        Button saveBtn = new Button("Update");
        saveBtn.setOnAction(e -> {
            try {
                selected.setFirstName(fnameField.getText());
                selected.setLastName(lnameField.getText());
                selected.setBaseSalary(BigDecimal.valueOf(Double.parseDouble(salaryField.getText())));

                dao.update(selected);
                refreshEmployeeTable();
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", "Could not update employee: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(
                new Label("First Name:"), fnameField,
                new Label("Last Name:"), lnameField,
                new Label("Salary:"), salaryField, saveBtn
        );

        Scene scene = new Scene(layout, 300, 250);
        dialog.setScene(scene);
        dialog.show();
    }

    // reusable alert message box
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}