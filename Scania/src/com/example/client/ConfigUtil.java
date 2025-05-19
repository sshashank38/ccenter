package com.example.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream in = new FileInputStream("C:\\Users\\Shashank\\eclipse-workspace\\Scania\\src\\com\\example\\client\\config.properties")) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("Error loading config.properties: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
