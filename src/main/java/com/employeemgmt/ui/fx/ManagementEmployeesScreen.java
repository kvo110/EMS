package com.employeemgmt.ui.fx;

import com.employeemgmt.models.Employee;
import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;
import com.employeemgmt.ui.fx.components.EmployeeForm;
import com.employeemgmt.ui.fx.components.EmployeeTable;
import com.employeemgmt.ui.fx.components.SearchBar;

import java.util.List;

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

/*
 * ManagementEmployeesScreen
 * -------------------------
 * Main CRUD UI for HR admins.
 * Layout: table on the left, form + buttons on the right.
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

        Label hint = new Label("Tip: type an ID or a name, then hit Search. Leave empty and click Show All for full list.");
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

        Label status = new Label();
        status.setStyle("-fx-text-fill: #444; -fx-font-size: 12px;");

        EmployeeTable table = new EmployeeTable();
        EmployeeForm form = new EmployeeForm();
        SearchBar search = new SearchBar();

        // search logic
        search.setSearchAction(query -> {
            SearchResult sr;

            if (query == null || query.isBlank()) {
                sr = employeeService.getAllEmployees(adminUser);
            } else if (query.trim().matches("\\d+")) {
                int id = Integer.parseInt(query.trim());
                sr = employeeService.searchEmployeeById(id, adminUser);
            } else {
                String[] parts = query.trim().split("\\s+", 2);
                String first = parts[0];
                String last = parts.length > 1 ? parts[1] : "";
                sr = employeeService.searchByName(first, last, adminUser);
            }

            if (!sr.isSuccess()) {
                table.update(List.of());
                status.setText(sr.getMessage());
                return;
            }

            table.update(sr.getEmployees());
            status.setText("Loaded " + sr.getCount() + " employee(s).");
        });

        // when user clicks a row, load into form
        table.setOnRowSelected(emp -> {
            if (emp == null) {
                form.clear();
                status.setText("Selection cleared.");
            } else {
                form.loadEmployee(emp);
                status.setText("Loaded employee " + emp.getEmpid() + " into form.");
            }
        });

        Button saveBtn = new Button("Save / Update");
        Button deleteBtn = new Button("Delete");
        Button clearBtn = new Button("Clear Form");
        Button backBtn = new Button("Back");

        saveBtn.setPrefWidth(130);
        deleteBtn.setPrefWidth(130);
        clearBtn.setPrefWidth(130);
        backBtn.setPrefWidth(130);

        saveBtn.setOnAction(e -> {
            Employee emp = form.buildEmployeeFromFields();
            if (!emp.isValid()) {
                status.setText("Please fill in all required fields (first name, last name, email, hire date, salary).");
                return;
            }

            SearchResult sr;
            if (emp.getEmpid() == 0) {
                sr = employeeService.addEmployee(emp, adminUser);
            } else {
                sr = employeeService.updateEmployee(emp, adminUser);
            }

            status.setText(sr.getMessage());
            if (sr.isSuccess()) {
                search.triggerRefresh();
                if (emp.getEmpid() == 0) {
                    form.clear();
                } else {
                    form.loadEmployee(emp);
                }
            }
        });

        deleteBtn.setOnAction(e -> {
            Integer id = form.getLoadedEmployeeId();
            if (id == null || id == 0) {
                status.setText("Select an employee from the table before deleting.");
                return;
            }

            SearchResult sr = employeeService.deleteEmployee(id, adminUser);
            status.setText(sr.getMessage());
            if (sr.isSuccess()) {
                form.clear();
                search.triggerRefresh();
            }
        });

        clearBtn.setOnAction(e -> {
            form.clear();
            status.setText("Form cleared.");
        });

        backBtn.setOnAction(e -> {
            stage.close();
            new AdminDashboard().start(new Stage(), adminUser);
        });

        HBox buttonRow = new HBox(10, saveBtn, deleteBtn, clearBtn, backBtn);
        buttonRow.setAlignment(Pos.CENTER);

        VBox rightPanel = new VBox(10, form.getNode(), buttonRow);
        rightPanel.setPadding(new Insets(10));

        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.HORIZONTAL);
        split.getItems().addAll(table.getNode(), rightPanel);
        split.setDividerPositions(0.55);

        BorderPane root = new BorderPane();
        root.setTop(new VBox(6, header, search.getNode(), hint));
        BorderPane.setMargin(root.getTop(), new Insets(10));
        root.setCenter(split);
        root.setBottom(status);
        BorderPane.setMargin(status, new Insets(6, 10, 8, 10));

        Scene scene = new Scene(root, 980, 540);
        stage.setTitle("Employee Management");
        stage.setScene(scene);
        stage.show();

        // load initial data
        search.triggerRefresh();
    }
}
