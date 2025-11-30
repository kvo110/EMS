package com.employeemgmt.ui.fx;

import javafx.application.Application;
import javafx.stage.Stage;

/*
   JavaFXUI
   This is just the bridge between JavaFX Application
   and our own LoginScreen.
*/
public class JavaFXUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        new LoginScreen().start(primaryStage);
    }
}