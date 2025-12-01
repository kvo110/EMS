package com.employeemgmt.ui.fx.components;

import com.employeemgmt.models.Employee;

import java.util.List;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/*
 * EmployeeTable
 * -------------
 * Thin wrapper around a TableView<Employee>.
 * Columns: ID, name, email, hire date, salary.
 */
public class EmployeeTable {

    private final TableView<Employee> table;
    private Consumer<Employee> rowSelectedHandler;

    public EmployeeTable() {
        table = new TableView<>();

        TableColumn<Employee, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("empid"));
        idCol.setPrefWidth(60);

        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> {
            Employee e = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(e.getFullName());
        });
        nameCol.setPrefWidth(160);

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<Employee, String> hireCol = new TableColumn<>("Hire Date");
        hireCol.setCellValueFactory(cellData -> {
            Employee e = cellData.getValue();
            String text = e.getHireDate() != null ? e.getHireDate().toString() : "";
            return new javafx.beans.property.SimpleStringProperty(text);
        });
        hireCol.setPrefWidth(100);

        TableColumn<Employee, String> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(cellData -> {
            Employee e = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(e.getFormattedSalary());
        });
        salaryCol.setPrefWidth(120);

        table.getColumns().addAll(idCol, nameCol, emailCol, hireCol, salaryCol);

        // listener for row selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, now) -> {
            if (rowSelectedHandler != null) {
                rowSelectedHandler.accept(now);
            }
        });
    }

    public void update(List<Employee> employees) {
        table.setItems(FXCollections.observableArrayList(employees));
    }

    public void setOnRowSelected(Consumer<Employee> handler) {
        this.rowSelectedHandler = handler;
    }

    public Node getNode() {
        return table;
    }
}
