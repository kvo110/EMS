package com.employeemgmt.ui.fx.components;

import com.employeemgmt.models.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

/*
    EmployeeTable
    -------------
    Wrapper around a TableView<Employee> used on the left side
    of the Manage Employees screen.
*/
public class EmployeeTable extends VBox {

    private final TableView<Employee> table;
    private final ObservableList<Employee> data = FXCollections.observableArrayList();

    public EmployeeTable() {
        table = new TableView<>();
        table.setItems(data);

        TableColumn<Employee, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("empid"));
        idCol.setPrefWidth(60);

        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c ->
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> c.getValue().getFullName()
                )
        );
        nameCol.setPrefWidth(160);

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<Employee, LocalDate> hireCol = new TableColumn<>("Hire Date");
        hireCol.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        hireCol.setPrefWidth(110);

        TableColumn<Employee, String> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(c ->
                javafx.beans.binding.Bindings.createStringBinding(
                        c.getValue()::getFormattedSalary
                )
        );
        salaryCol.setPrefWidth(110);

        table.getColumns().addAll(idCol, nameCol, emailCol, hireCol, salaryCol);

        setPadding(new Insets(10));
        getChildren().add(table);
    }

    public void setEmployees(List<Employee> employees) {
        data.setAll(employees);
    }

    public void setOnEmployeeSelected(Consumer<Employee> handler) {
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (handler != null) {
                handler.accept(newVal);
            }
        });
    }
}
