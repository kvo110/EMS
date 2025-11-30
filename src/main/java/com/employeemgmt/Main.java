package com.employeemgmt;

import com.employeemgmt.ui.console.ConsoleApp;
import com.employeemgmt.ui.fx.JavaFXUI;
import javafx.application.Application;

/*
   Main.java
   ----------
   Main launcher for the EMS project.

   - If you run with arg "console", it starts the text-based UI.
   - Otherwise it launches the JavaFX UI.
*/
public class Main {

    public static void main(String[] args) {

        // Option to run console mode for quick testing
        if (args.length > 0 && args[0].equalsIgnoreCase("console")) {
            System.out.println("Launching EMS Console Mode...");
            new ConsoleApp().start();
            return;
        }

        // Default: launch JavaFX application
        System.out.println("Launching EMS JavaFX UI...");
        Application.launch(JavaFXUI.class, args);
    }
}