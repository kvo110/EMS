package com.employeemgmt.ui.fx;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;
import com.employeemgmt.ui.fx.components.EmployeeForm;
import com.employeemgmt.ui.fx.components.EmployeeTable;
import com.employeemgmt.ui.fx.components.SearchBar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ManagementEmployeesScreen {

    private final EmployeeService employeeService = new EmployeeService();

    public void start(Stage stage, User adminUser) {

        // basic admin check
        if (adminUser == null || !adminUser.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label title = new Label("Employee Manager");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        SearchBar search = new SearchBar();
        EmployeeTable table = new EmployeeTable();
        EmployeeForm form = new EmployeeForm();

        Label status = new Label("");
        status.setStyle("-fx-text-fill: green;");

        // unified search: ID, name, DOB, SSN, email
        search.setSearchAction(query -> {
            String q = query.trim();

            if (q.isEmpty()) {
                var all = employeeService.getAllEmployees(adminUser);
                table.update(all.getEmployees());
                status.setText("Showing all employees (" + all.getCount() + ")");
                return;
            }

            SearchResult r = employeeService.searchAllFields(q, adminUser);
            table.update(r.getEmployees());
            status.setText("Matches: " + r.getCount());
        });

        // clicking a row fills the form with that employee’s info
        table.setOnRowSelected(emp -> {
            if (emp != null) {
                form.loadEmployee(emp);
            }
        });

        Button saveBtn = new Button("Save");
        Button deleteBtn = new Button("Delete");
        Button clearBtn = new Button("Clear Form");
        Button resetDBBtn = new Button("Reset View");
        Button backBtn = new Button("Back to Dashboard");

        // save new employee or update existing one
        saveBtn.setOnAction(e -> {
            Employee emp = form.buildEmployeeFromFields();

            if (emp == null) {
                status.setText("Please fill out fields correctly.");
                return;
            }

            boolean exists = emp.getEmpid() > 0;
            SearchResult r = exists
                    ? employeeService.updateEmployee(emp, adminUser)
                    : employeeService.addEmployee(emp, adminUser);

            // popup only on successful save
            if (r.isSuccess()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Employee saved successfully.");
                alert.showAndWait();

                // clear form so admin can add the next person
                form.clear();

                // reload table so new employee shows instantly
                var reload = employeeService.getAllEmployees(adminUser);
                table.update(reload.getEmployees());
                status.setText("Loaded " + reload.getCount() + " employees.");
                return;
            }

            status.setText(r.getMessage());
        });

        // delete employee
        deleteBtn.setOnAction(e -> {
            Integer id = form.getLoadedEmployeeId();

            if (id == null) {
                status.setText("Select an employee to delete.");
                return;
            }

            SearchResult r = employeeService.deleteEmployee(id, adminUser);
            status.setText(r.getMessage());
            form.clear();

            var reload = employeeService.getAllEmployees(adminUser);
            table.update(reload.getEmployees());
        });

        // clear only the input fields
        clearBtn.setOnAction(e -> form.clear());

        // reload every employee in the database
        resetDBBtn.setOnAction(e -> {
            var r = employeeService.getAllEmployees(adminUser);
            table.update(r.getEmployees());
            status.setText("Showing all employees (" + r.getCount() + ")");
        });

        // go back to the main admin dashboard
        backBtn.setOnAction(e -> {
            stage.close();
            new AdminDashboard().start(new Stage(), adminUser);
        });

        VBox rightPanel = new VBox(10,
                form.getNode(),
                saveBtn,
                deleteBtn,
                clearBtn,
                resetDBBtn,
                backBtn
        );

        rightPanel.setPadding(new Insets(10));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setStyle("-fx-background-color: #f4f6ff;");

        SplitPane split = new SplitPane(table.getNode(), rightPanel);
        split.setDividerPositions(0.55);

        VBox root = new VBox(
                12,
                title,
                search.getNode(),
                status,
                split
        );

        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #eef3ff;");

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Manage Employees");
        stage.setScene(scene);
        stage.show();

        // load all employees when the screen opens
        var load = employeeService.getAllEmployees(adminUser);
        table.update(load.getEmployees());
        status.setText("Loaded " + load.getCount() + " employees.");
    }
}
