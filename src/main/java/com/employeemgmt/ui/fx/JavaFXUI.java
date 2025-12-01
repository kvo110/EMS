package com.employeemgmt.ui.fx;

import javafx.application.Application;
import javafx.stage.Stage;

/*
   Just boots the app on the login screen.
*/
public class JavaFXUI extends Application {
    @Override
    public void start(Stage stage) {
        new LoginScreen().start(stage);
    }
}
