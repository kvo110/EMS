package com.employeemgmt.ui.fx;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;
import com.employeemgmt.ui.fx.components.EmployeeForm;
import com.employeemgmt.ui.fx.components.EmployeeTable;
import com.employeemgmt.ui.fx.components.SearchBar;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/*
    ManagementEmployeesScreen
    -------------------------
    CRUD UI for HR Admins.

    Layout:
    - Top: title + search bar
    - Left: table of employees
    - Right: editable form + buttons
    - Bottom: status label
*/
public class ManagementEmployeesScreen {

    private final EmployeeService employeeService = new EmployeeService();

    public void start(Stage stage, User adminUser) {
        if (adminUser == null || !adminUser.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label title = new Label("Manage Employees");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        SearchBar searchBar = new SearchBar("Search by name...");
        EmployeeTable table = new EmployeeTable();
        EmployeeForm form = new EmployeeForm();

        Label status = new Label("Ready.");
        status.setStyle("-fx-text-fill: #444; -fx-font-size: 12px;");

        // hook up search action
        searchBar.setOnSearch(query -> {
            SearchResult result;

            if (query == null || query.isBlank()) {
                result = employeeService.getAllEmployees(adminUser);
            } else {
                // for now we treat the text as both first and last name filter
                result = employeeService.searchByName(query.trim(), query.trim(), adminUser);
            }

            List<Employee> employees = result.getEmployees();
            table.setEmployees(employees);

            if (result.isSuccess()) {
                status.setText("Search complete. " + employees.size() + " result(s).");
            } else {
                status.setText("Search error: " + result.getMessage());
            }
        });

        // table row selection → load into form
        table.setOnEmployeeSelected(emp -> {
            if (emp == null) {
                form.clearForm();
                return;
            }
            form.loadEmployee(emp);
            status.setText("Loaded employee ID " + emp.getEmpid() + " into form.");
        });

        // form action buttons
        Button newBtn = new Button("New");
        Button saveBtn = new Button("Save");
        Button deleteBtn = new Button("Delete");
        Button backBtn = new Button("Back to Admin");

        newBtn.setOnAction(e -> {
            form.clearForm();
            status.setText("Form cleared. You can enter a new employee.");
        });

        saveBtn.setOnAction(e -> {
            try {
                Employee emp = form.buildEmployeeFromForm();

                if (!emp.isValid()) {
                    status.setText("Please fill in all required fields for employee.");
                    return;
                }

                SearchResult result;

                // if empid == 0, treat this as new row
                if (emp.getEmpid() == 0) {
                    result = employeeService.addEmployee(emp, adminUser);
                } else {
                    result = employeeService.updateEmployee(emp, adminUser);
                }

                if (result.isSuccess()) {
                    status.setText(result.getMessage());
                    searchBar.triggerSearch();  // refresh table
                    if (!result.getEmployees().isEmpty()) {
                        form.loadEmployee(result.getEmployees().get(0));
                    }
                } else {
                    status.setText("Save failed: " + result.getMessage());
                }

            } catch (Exception ex) {
                status.setText("Error saving employee: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Integer id = form.getCurrentEmployeeId();
            if (id == null || id == 0) {
                status.setText("No employee selected to delete.");
                return;
            }

            SearchResult result = employeeService.deleteEmployee(id, adminUser);
            if (result.isSuccess()) {
                status.setText("Employee deleted.");
                form.clearForm();
                searchBar.triggerSearch();
            } else {
                status.setText("Delete failed: " + result.getMessage());
            }
        });

        backBtn.setOnAction(e -> {
            stage.close();
            new AdminDashboard().start(new Stage(), adminUser);
        });

        VBox rightPanel = new VBox(
                10,
                form,
                new HBox(10, newBtn, saveBtn, deleteBtn),
                backBtn
        );
        rightPanel.setAlignment(Pos.TOP_LEFT);
        rightPanel.setPadding(new Insets(10));

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().addAll(table, rightPanel);
        splitPane.setDividerPositions(0.55);

        VBox topBox = new VBox(8, title, searchBar);
        topBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(splitPane);
        root.setBottom(status);
        BorderPane.setMargin(status, new Insets(5, 10, 10, 10));

        Scene scene = new Scene(root, 900, 520);
        stage.setTitle("Manage Employees");
        stage.setScene(scene);
        stage.show();

        // initial load
        searchBar.triggerSearch();
    }
}
