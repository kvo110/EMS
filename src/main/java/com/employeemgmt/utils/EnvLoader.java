package com.employeemgmt.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Environment Variable Loader
 * Loads environment variables from .env file for secure configuration
 */
public class EnvLoader {
    
    private static Map<String, String> envVars = new HashMap<>();
    private static boolean loaded = false;
    
    /**
     * Load environment variables from .env file
     */
    public static void loadEnv() {
        if (loaded) return;
        
        String envFile = ".env";
        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parse key=value pairs
                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();
                    envVars.put(key, value);
                }
            }
            loaded = true;
            System.out.println("✅ Environment variables loaded successfully");
        } catch (IOException e) {
            System.err.println("⚠️ Warning: Could not load .env file. Using default properties.");
            System.err.println("Make sure .env file exists in project root directory.");
        }
    }
    
    /**
     * Get environment variable value
     * @param key The environment variable key
     * @return The value, or null if not found
     */
    public static String getEnv(String key) {
        if (!loaded) {
            loadEnv();
        }
        return envVars.get(key);
    }
    
    /**
     * Get environment variable with default value
     * @param key The environment variable key
     * @param defaultValue Default value if key not found
     * @return The value or default value
     */
    public static String getEnv(String key, String defaultValue) {
        String value = getEnv(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Check if environment variables are loaded
     * @return true if loaded, false otherwise
     */
    public static boolean isLoaded() {
        return loaded;
    }
}
