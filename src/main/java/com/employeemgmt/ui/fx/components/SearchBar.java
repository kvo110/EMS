package com.employeemgmt.ui.fx.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

// Small reusable search bar component
// Just wraps a TextField + a Search button
public class SearchBar {

    private final TextField queryField;
    private final Button searchButton;

    private Consumer<String> searchHandler;

    public SearchBar(String placeholder) {
        queryField = new TextField();
        queryField.setPromptText(placeholder);

        searchButton = new Button("Search");

        searchButton.setOnAction(e -> performSearch());
        queryField.setOnAction(e -> performSearch());
    }

    private void performSearch() {
        if (searchHandler != null) {
            searchHandler.accept(queryField.getText());
        }
    }

    // Called by screens to wire up the actual search logic
    public void setOnSearch(Consumer<String> handler) {
        this.searchHandler = handler;
    }

    // Handy to trigger an initial "load all" search
    public void triggerInitialSearch() {
        if (searchHandler != null) {
            searchHandler.accept("");
        }
    }

    public Node getNode() {
        HBox box = new HBox(8, queryField, searchButton);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 5, 0));
        return box;
    }
}
