package com.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORT = 8080;

    static void main() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("HTTP server started on PORT: " + PORT + "...");

            while (true) {
                try {
                    Socket socket = serverSocket.accept();

                    ClientHandler clientHandler = new ClientHandler(socket);

                    Thread thread = new Thread(clientHandler);

                    thread.start();
                } catch (IOException e) {
                    System.err.println("Failed to accept connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT + " - " + e.getMessage());
        }
    }
}
