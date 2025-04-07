package com.example.client;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerUtil {
    private static final String LOG_FILE = "C:\\temp\\TestCompany\\logs\\application.log";
    private static PrintWriter logWriter;

    static {
        try {
            File logFile = new File(LOG_FILE);
            logFile.getParentFile().mkdirs(); // Ensure directories exist
            logWriter = new PrintWriter(new FileWriter(logFile, true), true);
        } catch (IOException e) {
            System.err.println("⚠️ Failed to initialize logger: " + e.getMessage());
        }
    }

    public static synchronized void log(String level, String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logEntry = "[" + timestamp + "] [" + level + "] " + message;

        // Print to console
        System.out.println(logEntry);

        // Write to log file
        if (logWriter != null) {
            logWriter.println(logEntry);
        }
    }

    public static void info(String message) {
        log("INFO", message);
    }

    public static void warning(String message) {
        log("WARNING", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    public static void closeLogger() {
        if (logWriter != null) {
            logWriter.close();
        }
    }
}