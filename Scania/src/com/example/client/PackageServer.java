package com.example.client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class PackageServer {
    private static final int PORT = Integer.parseInt(ConfigUtil.get("server.port"));;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeServerSocket();
        }
    }

    private static void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server socket closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Response processRequest(Request request) {
        Response response = new Response();
        int option = request.getOption();
        String fileKey = request.getFileKey();

        try {
            switch (option) {
                case 1:
                    response.setData(XMLToJsonFileConverter.listXmlFiles());
                    break;
                case 2:
                    response = XMLToJsonFileConverter.getPackageDetails();
                    break;
                case 3:
                    response = XMLToJsonFileConverter.getPartDetails(request.getFileKey());
                    break;
                case 4:
                    response = XMLToJsonFileConverter.getDocumentDetails(request.getFileKey());
                    break;
                case 5:
                    response = XMLToJsonFileConverter.convertXmlToJson(fileKey);
                    break;
                case 6:
                    response = JsonToPostgresLoader.loadJsonToDatabase(fileKey); // You'll need to implement this method
                    break;
                case 7:
					closeServerSocket();

                default:
                    response.setError("Invalid option.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setError("Server error: " + e.getMessage());
        }
        return response;
    }

    static class ClientHandler extends Thread {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

                while (true) {
                    try {
                        Object received = input.readObject();
                        if (received == null) break;

                        Request request = (Request) received;
                        Response response = PackageServer.processRequest(request); // ✅ Now calling static method

                        output.writeObject(response);
                        output.flush();

                    } catch (EOFException e) {
                        System.out.println("Client disconnected.");
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    System.out.println("Client socket closed.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
