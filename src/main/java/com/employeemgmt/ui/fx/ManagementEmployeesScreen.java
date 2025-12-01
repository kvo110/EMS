package com.employeemgmt.ui.fx;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;
import com.employeemgmt.ui.fx.components.EmployeeForm;
import com.employeemgmt.ui.fx.components.EmployeeTable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

// Admin-only screen for real employee CRUD.
// Layout: table on the left, form + buttons on the right.
public class ManagementEmployeesScreen {

    private final EmployeeService employeeService = new EmployeeService();

    public void start(Stage stage, User adminUser) {
        if (adminUser == null || !adminUser.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label header = new Label("Manage Employees");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label hint = new Label("Tip: use the search fields to filter, then edit on the right.");
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

        // Search fields (kept simple)
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        Button searchBtn = new Button("Search");
        Button resetBtn = new Button("Reset");

        HBox searchBox = new HBox(8, new Label("Search:"), firstNameField, lastNameField, searchBtn, resetBtn);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Left: table of employees
        EmployeeTable table = new EmployeeTable();

        // Right: form to edit / create
        EmployeeForm form = new EmployeeForm();

        Label status = new Label();
        status.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");

        Button saveBtn = new Button("Save / Update");
        Button newBtn = new Button("New Employee");
        Button deleteBtn = new Button("Delete");
        Button backBtn = new Button("Back");

        saveBtn.setPrefWidth(140);
        newBtn.setPrefWidth(140);
        deleteBtn.setPrefWidth(140);
        backBtn.setPrefWidth(140);

        // Search logic
        searchBtn.setOnAction(e -> {
            String first = firstNameField.getText().trim();
            String last = lastNameField.getText().trim();

            SearchResult sr = employeeService.searchByName(first, last, adminUser);
            updateTableAndStatus(table, status, sr);
        });

        // Reset just loads everyone again
        resetBtn.setOnAction(e -> {
            firstNameField.clear();
            lastNameField.clear();
            loadAllEmployees(table, status, adminUser);
        });

        // When a row is selected, load into form
        table.setOnRowSelected(emp -> {
            if (emp == null) {
                form.clear();
            } else {
                form.loadEmployee(emp);
            }
        });

        // Save / Update handler
        saveBtn.setOnAction(e -> {
            Employee emp = form.buildEmployeeFromFields();
            if (emp == null || !emp.isValid()) {
                status.setText("Please fill in all required fields before saving.");
                return;
            }

            if (form.getLoadedEmployeeId() == null) {
                // New employee
                SearchResult sr = employeeService.addEmployee(emp, adminUser);
                updateTableAndStatus(table, status, sr);
                if (sr.isSuccess()) {
                    form.loadEmployee(sr.getEmployees().get(0));
                }
            } else {
                // Existing employee
                SearchResult sr = employeeService.updateEmployee(emp, adminUser);
                updateTableAndStatus(table, status, sr);
            }
        });

        // Clear form for new entry
        newBtn.setOnAction(e -> form.clear());

        // Delete selected employee
        deleteBtn.setOnAction(e -> {
            Integer id = form.getLoadedEmployeeId();
            if (id == null) {
                status.setText("Select an employee in the table before deleting.");
                return;
            }

            SearchResult sr = employeeService.deleteEmployee(id, adminUser);
            status.setText(sr.getMessage());
            loadAllEmployees(table, status, adminUser);
            form.clear();
        });

        backBtn.setOnAction(e -> {
            stage.close();
            new AdminDashboard().start(new Stage(), adminUser);
        });

        // Right side layout
        VBox rightPanel = new VBox(
                10,
                form.getNode(),
                new HBox(10, saveBtn, newBtn, deleteBtn),
                backBtn,
                status
        );
        rightPanel.setPadding(new Insets(10));
        rightPanel.setAlignment(Pos.TOP_LEFT);

        // SplitPane: table on left, form on right
        SplitPane split = new SplitPane(table.getNode(), rightPanel);
        split.setOrientation(Orientation.HORIZONTAL);
        split.setDividerPositions(0.5);

        VBox topBox = new VBox(6, header, hint, searchBox);
        topBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(split);

        Scene scene = new Scene(root, 900, 520);
        stage.setTitle("Manage Employees");
        stage.setScene(scene);
        stage.show();

        // Initial load: all employees
        loadAllEmployees(table, status, adminUser);
    }

    private void loadAllEmployees(EmployeeTable table, Label status, User adminUser) {
        SearchResult sr = employeeService.getAllEmployees(adminUser);
        updateTableAndStatus(table, status, sr);
    }

    private void updateTableAndStatus(EmployeeTable table, Label status, SearchResult sr) {
        List<Employee> employees = sr.getEmployees();
        table.update(employees);
        status.setText(sr.getMessage() + " (" + employees.size() + " record(s))");
    }
}
