package com.employeemgmt.ui.fx.components;

import com.employeemgmt.models.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

// simple table wrapper so UI code stays clean
public class EmployeeTable {

    private final TableView<Employee> table;
    private final ObservableList<Employee> data = FXCollections.observableArrayList();

    public EmployeeTable() {
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Employee, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("empid"));

        TableColumn<Employee, String> colFirst = new TableColumn<>("First");
        colFirst.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, String> colLast = new TableColumn<>("Last");
        colLast.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Employee, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(colId, colFirst, colLast, colEmail);
        table.setItems(data);
    }

    // updates the table rows
    public void update(java.util.List<Employee> list) {
        data.setAll(list);
    }

    // callback for when user selects a row
    public void setOnRowSelected(java.util.function.Consumer<Employee> cb) {
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) cb.accept(newVal);
        });
    }

    // returns the actual TableView node
    public Node getNode() {
        return table;
    }
}
