package com.employeemgmt.ui.fx.components;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/*
 * SearchBar
 * ---------
 * Reusable search bar with:
 * - Text field
 * - Search button
 * - Show All button
 * It just calls a Consumer<String> with the current query.
 */
public class SearchBar {

    private final HBox root;
    private final TextField queryField;
    private Consumer<String> searchAction;

    public SearchBar() {
        queryField = new TextField();
        queryField.setPromptText("Search by ID or name...");

        Button searchBtn = new Button("Search");
        Button showAllBtn = new Button("Show All");

        searchBtn.setOnAction(e -> fireSearch());
        showAllBtn.setOnAction(e -> {
            queryField.clear();
            fireSearch();
        });

        root = new HBox(8, queryField, searchBtn, showAllBtn);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(5, 0, 5, 0));
    }

    private void fireSearch() {
        if (searchAction != null) {
            searchAction.accept(queryField.getText());
        }
    }

    public void setSearchAction(Consumer<String> action) {
        this.searchAction = action;
    }

    public void triggerRefresh() {
        fireSearch();
    }

    public Node getNode() {
        return root;
    }
}
