package com.employeemgmt.ui.fx.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

/*
    SearchBar
    ---------
    Tiny reusable search bar:
    [ text field ] [ Search ] [ Clear ]
*/
public class SearchBar extends HBox {

    private final TextField field;
    private Consumer<String> onSearch;

    public SearchBar(String prompt) {
        this.field = new TextField();
        this.field.setPromptText(prompt);

        Button searchBtn = new Button("Search");
        Button clearBtn = new Button("Clear");

        searchBtn.setOnAction(e -> triggerSearch());
        clearBtn.setOnAction(e -> {
            field.clear();
            triggerSearch();
        });

        setSpacing(8);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(5, 0, 5, 0));
        getChildren().addAll(field, searchBtn, clearBtn);
    }

    public void setOnSearch(Consumer<String> handler) {
        this.onSearch = handler;
    }

    public void triggerSearch() {
        if (onSearch != null) {
            onSearch.accept(field.getText());
        }
    }
}
