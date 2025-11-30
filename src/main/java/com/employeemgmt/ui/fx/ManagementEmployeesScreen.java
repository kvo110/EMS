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
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/*
   ManagementEmployeesScreen
   Full CRUD admin screen:
   - Search by ID or name
   - View table of employees
   - Edit or add employees via form
   - Delete employees
*/
public class ManagementEmployeesScreen {

    private final EmployeeService employeeService = new EmployeeService();

    public void start(Stage stage, User adminUser) {
        if (adminUser == null || !adminUser.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label header = new Label("Employee Management");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        SearchBar searchBar = new SearchBar();
        EmployeeTable table = new EmployeeTable();
        EmployeeForm form = new EmployeeForm();

        TextArea status = new TextArea();
        status.setEditable(false);
        status.setPrefRowCount(3);

        Button addBtn = new Button("Add New");
        Button saveBtn = new Button("Save / Update");
        Button deleteBtn = new Button("Delete");
        Button clearBtn = new Button("Clear Form");

        for (Button b : new Button[]{addBtn, saveBtn, deleteBtn, clearBtn}) {
            b.setPrefWidth(140);
        }

        // Search logic
        searchBar.getSearchButton().setOnAction(e -> {
            String idText = searchBar.getIdText().trim();
            String first = searchBar.getFirstNameText().trim();
            String last = searchBar.getLastNameText().trim();

            SearchResult result;

            if (!idText.isEmpty()) {
                try {
                    int id = Integer.parseInt(idText);
                    result = employeeService.searchEmployeeById(id, adminUser);
                } catch (NumberFormatException ex) {
                    status.setText("Employee ID must be a number.");
                    return;
                }
            } else if (!first.isEmpty() || !last.isEmpty()) {
                result = employeeService.searchByName(first, last, adminUser);
            } else {
                result = employeeService.getAllEmployees(adminUser);
            }

            table.setEmployees(result.getEmployees());
            status.setText(result.getMessage() + " (rows: " + result.getCount() + ")");
        });

        searchBar.getShowAllButton().setOnAction(e -> {
            SearchResult result = employeeService.getAllEmployees(adminUser);
            table.setEmployees(result.getEmployees());
            status.setText(result.getMessage() + " (rows: " + result.getCount() + ")");
        });

        table.setOnRowSelected(emp -> {
            if (emp != null) {
                form.loadEmployee(emp);
            }
        });

        addBtn.setOnAction(e -> {
            try {
                Employee emp = form.buildEmployeeFromFields();
                emp.setEmpid(0); // let DB assign ID

                SearchResult result = employeeService.addEmployee(emp, adminUser);

                if (!result.isSuccess()) {
                    status.setText(result.getMessage());
                    return;
                }

                SearchResult refreshed = employeeService.getAllEmployees(adminUser);
                table.setEmployees(refreshed.getEmployees());
                status.setText("Employee added. " + refreshed.getMessage());
                form.clear();

            } catch (IllegalArgumentException ex) {
                status.setText("Form error: " + ex.getMessage());
            }
        });

        saveBtn.setOnAction(e -> {
            Integer id = form.getLoadedEmployeeId();
            if (id == null) {
                status.setText("Load an employee or enter an ID before updating.");
                return;
            }

            try {
                Employee emp = form.buildEmployeeFromFields();
                emp.setEmpid(id);

                SearchResult result = employeeService.updateEmployee(emp, adminUser);

                if (!result.isSuccess()) {
                    status.setText(result.getMessage());
                    return;
                }

                SearchResult refreshed = employeeService.getAllEmployees(adminUser);
                table.setEmployees(refreshed.getEmployees());
                status.setText("Employee updated. " + refreshed.getMessage());

            } catch (IllegalArgumentException ex) {
                status.setText("Form error: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Integer id = form.getLoadedEmployeeId();
            if (id == null) {
                status.setText("Load an employee or enter an ID before deleting.");
                return;
            }

            SearchResult result = employeeService.deleteEmployee(id, adminUser);

            if (!result.isSuccess()) {
                status.setText(result.getMessage());
                return;
            }

            SearchResult refreshed = employeeService.getAllEmployees(adminUser);
            table.setEmployees(refreshed.getEmployees());
            status.setText("Employee deleted. " + refreshed.getMessage());
            form.clear();
        });

        clearBtn.setOnAction(e -> form.clear());

        VBox rightPanel = new VBox(
                10,
                new Label("Employee Details"),
                form,
                new HBox(10, addBtn, saveBtn),
                new HBox(10, deleteBtn, clearBtn)
        );
        rightPanel.setPadding(new Insets(10));
        rightPanel.setAlignment(Pos.TOP_CENTER);

        SplitPane split = new SplitPane(table, rightPanel);
        split.setOrientation(Orientation.HORIZONTAL);
        split.setDividerPositions(0.55);

        BorderPane root = new BorderPane();
        root.setTop(new VBox(8, header, searchBar));
        BorderPane.setMargin(header, new Insets(10, 10, 0, 10));

        root.setCenter(split);
        root.setBottom(status);
        BorderPane.setMargin(status, new Insets(8));

        Scene scene = new Scene(root, 960, 540);
        stage.setTitle("Manage Employees");
        stage.setScene(scene);
        stage.show();

        // Initial load
        SearchResult initial = employeeService.getAllEmployees(adminUser);
        table.setEmployees(initial.getEmployees());
        status.setText(initial.getMessage() + " (rows: " + initial.getCount() + ")");
    }
}