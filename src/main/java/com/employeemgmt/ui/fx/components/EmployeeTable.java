package com.employeemgmt.ui.fx.components;

import com.employeemgmt.models.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.util.List;
import java.util.function.Consumer;

// Wrapper around TableView<Employee> so the main screen code stays cleaner.
public class EmployeeTable {

    private final TableView<Employee> tableView;
    private final BorderPane root;
    private Consumer<Employee> rowSelectHandler;

    public EmployeeTable() {
        tableView = new TableView<>();

        TableColumn<Employee, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("empid"));
        idCol.setPrefWidth(60);

        TableColumn<Employee, String> firstCol = new TableColumn<>("First Name");
        firstCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstCol.setPrefWidth(130);

        TableColumn<Employee, String> lastCol = new TableColumn<>("Last Name");
        lastCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastCol.setPrefWidth(130);

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<Employee, String> hireCol = new TableColumn<>("Hire Date");
        hireCol.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        hireCol.setPrefWidth(110);

        TableColumn<Employee, String> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("formattedSalary"));
        salaryCol.setPrefWidth(120);

        tableView.getColumns().addAll(idCol, firstCol, lastCol, emailCol, hireCol, salaryCol);

        // When the selection changes, call the handler if present
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (rowSelectHandler != null) {
                rowSelectHandler.accept(newVal);
            }
        });

        Label title = new Label("Employees");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        root = new BorderPane();
        root.setTop(title);
        BorderPane.setMargin(title, new Insets(5));
        root.setCenter(tableView);
        root.setPadding(new Insets(5));
    }

    public Node getNode() {
        return root;
    }

    // Replace table contents
    public void update(List<Employee> employees) {
        ObservableList<Employee> list = FXCollections.observableArrayList(employees);
        tableView.setItems(list);
    }

    public void setOnRowSelected(Consumer<Employee> handler) {
        this.rowSelectHandler = handler;
    }
}
