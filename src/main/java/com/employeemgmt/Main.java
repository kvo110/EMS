package com.employeemgmt;

import com.employeemgmt.ui.ConsoleUI;
import com.employeemgmt.utils.EnvLoader;

/**
 * Main class for Employee Management System
 * 
 * Enhanced application entry point with professional Console UI
 * - Initialize database connection and environment
 * - Launch enhanced user interface
 * - Handle application lifecycle and graceful shutdown
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            // Load environment variables
            EnvLoader.loadEnv();
            
            // Initialize and start the enhanced Console UI
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.start();
            
        } catch (Exception e) {
            System.err.println("Fatal error starting Employee Management System:");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
