package com.example.client;

import java.io.*;
import java.io.ObjectInputFilter.Config;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Logger;

public class PackageClient {
    private static final String SERVER_ADDRESS = ConfigUtil.get("server.address");
    private static final int SERVER_PORT = Integer.parseInt(ConfigUtil.get("server.port"));
    private static final Logger logger = Logger.getLogger(PackageClient.class.getName());

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            logInfo("Connected to server.");

            while (true) {
                System.out.println("\n------ MENU ------");
                System.out.println("1. List available XML files");
                System.out.println("2. Get package details");
                System.out.println("3. Get part details");
                System.out.println("4. Get document details");
                System.out.println("5. Convert XML to JSON");
                System.out.println("6. Load Json to PostgreSQL Database");
                System.out.println("7. Exit");
                System.out.println("------------------");

                int option = getUserOption(scanner);
                
                Request request = new Request(option);
                switch (option) {
                    case 1:
                    case 2:
                        break;
                    case 3:
                        System.out.print("Enter part number: ");
                        request.setFileKey(scanner.nextLine().trim());
                        break;
                    case 4:
                        System.out.print("Enter Document number: ");
                        request.setFileKey(scanner.nextLine().trim());
                        break;
                    case 5:
                        System.out.print("Enter file key: ");
                        request.setFileKey(scanner.nextLine().trim());
                        break;
                    case 6:
                        System.out.print("Enter file key (without .json): ");
                        request.setFileKey(scanner.nextLine());
                        break;
                    case 7:
                        logInfo("Exiting client.");
                        System.out.println("Exiting client. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Please select a valid option.");
                        continue;
                }

                sendRequest(output, request);
                handleResponse(input);
            }
        } catch (IOException e) {
            logSevere("Connection error: " + e.getMessage());
        }
    }

    private static int getUserOption(Scanner scanner) {
        int option;
        while (true) {
            try {
                System.out.print("Enter option: ");
                option = Integer.parseInt(scanner.nextLine().trim());
                if (option >= 1 && option <= 6) {
                    return option;
                } else {
                    System.out.println("Invalid option. Please select a valid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static void sendRequest(ObjectOutputStream output, Request request) {
        try {
            output.writeObject(request);
            output.flush();
            logInfo("Request sent to server: " + request);
        } catch (IOException e) {
            logSevere("Error sending request: " + e.getMessage());
        }
    }

    private static void handleResponse(ObjectInputStream input) {
        try {
            Response response = (Response) input.readObject();
            if (response.getError() != null) {
                logWarning("Server error: " + response.getError());
                System.out.println("Error: " + response.getError());
            } else {
                System.out.println("\n------ SERVER RESPONSE ------");
                System.out.println(response.getData());
                System.out.println("-----------------------------");
                logInfo("Response received.");
            }
        } catch (EOFException e) {
            logSevere("Connection lost. Server closed the connection.");
        } catch (IOException | ClassNotFoundException e) {
            logSevere("Error reading response: " + e.getMessage());
        }
    }

    private static void logInfo(String message) {
        logger.info("[" + getCurrentTimestamp() + "]: " + message);
    }

    private static void logWarning(String message) {
        logger.warning("WARNING[" + getCurrentTimestamp() + "]: " + message);
    }

    private static void logSevere(String message) {
        logger.severe("ERROR[" + getCurrentTimestamp() + "]: " + message);
    }

    private static String getCurrentTimestamp() {
        return new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a").format(new Date());
    }
}
