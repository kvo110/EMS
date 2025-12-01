// SearchBar.java (cleaned + reliable real-time search)
package com.employeemgmt.ui.fx.components;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SearchBar {

    private final TextField field = new TextField();
    private final Button searchBtn = new Button("Search");

    // callback for whatever screen uses this
    public interface SearchAction {
        void execute(String query);
    }

    private SearchAction action;

    public SearchBar() {
        field.setPromptText("Search employees...");

        // real-time search (only when at least 1 character is typed)
        field.textProperty().addListener((obs, oldV, newV) -> {
            if (action != null) {
                String q = newV.trim();
                if (!q.isEmpty()) action.execute(q);
            }
        });

        // manual search button
        searchBtn.setOnAction(e -> {
            if (action != null) action.execute(field.getText().trim());
        });
    }

    public void setSearchAction(SearchAction a) {
        this.action = a;
    }

    // lets parent screen force a refresh
    public void triggerRefresh() {
        if (action != null) action.execute(field.getText().trim());
    }

    public Node getNode() {
        HBox box = new HBox(10, field, searchBtn);
        box.setPadding(new Insets(5));
        return box;
    }
}
