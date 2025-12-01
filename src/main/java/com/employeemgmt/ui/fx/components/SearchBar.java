package com.employeemgmt.ui.fx.components;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/*
   SearchBar
   ---------
   Now supports live refresh by calling the assigned callback
   every time text changes OR when the Search button is pressed.
*/
public class SearchBar {

    private final TextField field = new TextField();
    private final Button searchBtn = new Button("Search");

    private SearchAction action;

    // Functional interface for controller callbacks
    public interface SearchAction {
        void execute(String query);
    }

    public SearchBar() {
        field.setPromptText("Search employees...");
        field.textProperty().addListener((obs, oldV, newV) -> {
            if (action != null) action.execute(newV.trim());
        });

        searchBtn.setOnAction(e -> {
            if (action != null) action.execute(field.getText().trim());
        });
    }

    public void setSearchAction(SearchAction a) {
        this.action = a;
    }

    public void triggerRefresh() {
        if (action != null) action.execute(field.getText().trim());
    }

    public Node getNode() {
        HBox box = new HBox(10, field, searchBtn);
        box.setPadding(new Insets(5));
        return box;
    }
}
