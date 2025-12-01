package com.employeemgmt.ui.fx;

import javafx.application.Application;
import javafx.stage.Stage;

/*
    JavaFXUI
    --------
    Tiny bridge class between JavaFX and our own screens.
    Main.java launches this, and from here we just open LoginScreen.
*/
public class JavaFXUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // start the app on the login page
        new LoginScreen().start(primaryStage);
    }
}
