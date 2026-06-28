package com.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int PORT = 8080;

    static void main() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new java.net.InetSocketAddress(PORT));

            System.out.println("HTTP server started on PORT: " + PORT + " using Virtual Threads...");

            try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        ClientHandler clientHandler = new ClientHandler(socket);
                        executorService.submit(clientHandler);
                    } catch (IOException e) {
                        System.err.println("Failed to accept connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT + " - " + e.getMessage());
        }
    }
}
