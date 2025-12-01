package com.employeemgmt.ui.fx;

import javafx.application.Application;
import javafx.stage.Stage;

// Tiny bridge between JavaFX and our own screens.
// Main.java launches this class.
public class JavaFXUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Always start on the login screen
        new LoginScreen().start(primaryStage);
    }
}
