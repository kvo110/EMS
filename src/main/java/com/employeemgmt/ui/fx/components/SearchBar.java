package com.employeemgmt.ui.fx.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/*
   SearchBar
   Small search panel used by ManagementEmployeesScreen.

   Supports:
   - search by ID
   - search by first/last name
   - show all
*/
public class SearchBar extends VBox {

    private final TextField idField = new TextField();
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();

    private final Button searchButton = new Button("Search");
    private final Button showAllButton = new Button("Show All");

    public SearchBar() {
        Label label = new Label("Search Employees");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        idField.setPromptText("ID");
        idField.setPrefWidth(80);

        firstNameField.setPromptText("First name");
        lastNameField.setPromptText("Last name");

        HBox row = new HBox(8, idField, firstNameField, lastNameField, searchButton, showAllButton);
        row.setAlignment(Pos.CENTER_LEFT);

        setSpacing(6);
        setPadding(new Insets(10));
        getChildren().addAll(label, row);
    }

    public String getIdText() {
        return idField.getText();
    }

    public String getFirstNameText() {
        return firstNameField.getText();
    }

    public String getLastNameText() {
        return lastNameField.getText();
    }

    public Button getSearchButton() {
        return searchButton;
    }

    public Button getShowAllButton() {
        return showAllButton;
    }
}