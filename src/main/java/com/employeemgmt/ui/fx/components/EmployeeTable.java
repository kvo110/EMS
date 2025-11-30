package com.employeemgmt.ui.fx.components;

import com.employeemgmt.models.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

/*
   EmployeeTable
   Simple TableView wrapper just for employees.
*/
public class EmployeeTable extends VBox {

    private final TableView<Employee> table;
    private Consumer<Employee> rowSelectHandler;

    public EmployeeTable() {
        Label title = new Label("Employees");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Employee, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("empid"));

        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(
                        cellData.getValue()::getFullName));

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, String> hireCol = new TableColumn<>("Hire Date");
        hireCol.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    if (cellData.getValue().getHireDate() == null) return "";
                    return cellData.getValue().getHireDate().toString();
                }));

        TableColumn<Employee, String> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(
                        cellData.getValue()::getFormattedSalary));

        table.getColumns().addAll(idCol, nameCol, emailCol, hireCol, salaryCol);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (rowSelectHandler != null && newSel != null) {
                rowSelectHandler.accept(newSel);
            }
        });

        setPadding(new Insets(10));
        setSpacing(5);
        getChildren().addAll(title, table);
    }

    public void setEmployees(List<Employee> employees) {
        ObservableList<Employee> data = FXCollections.observableArrayList(employees);
        table.setItems(data);
    }

    public void clear() {
        table.getItems().clear();
    }

    public void setOnRowSelected(Consumer<Employee> handler) {
        this.rowSelectHandler = handler;
    }
}