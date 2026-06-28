package com.app;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORT = 8080;

    static void main() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("HTTP server started on PORT: " + PORT + "...");

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String requestLine = in.readLine();

                    if (requestLine == null) continue;

                    System.out.println("Zone 1 (Request Line): " + requestLine);

                    String[] zone1Parts = requestLine.split(" ");
                    String method = zone1Parts[0];
                    String path = zone1Parts[1];

                    String headerLine;
                    while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                        System.out.println("Zone 2 (Header Line): " + headerLine);
                    }

                    String htmlBody;
                    String statusLine;

                    switch (path) {
                        case "/" -> {
                            statusLine = "HTTP/1.1 200 OK";
                            htmlBody = "<h1>Hello, World!</h1>";
                        }
                        case "/about" -> {
                            statusLine = "HTTP/1.1 200 OK";
                            htmlBody = "<h1>About World!</h1>";
                        }
                        case "/contact" -> {
                            statusLine = "HTTP/1.1 200 OK";
                            htmlBody = "<h1>Contact World!</h1>";
                        }
                        default -> {
                            statusLine = "HTTP/1.1 404 Not Found";
                            htmlBody = "<h1>404 Not Found</h1>";
                        }
                    }

                    BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
                    out.write((statusLine + "\r\n").getBytes());
                    out.write("Content-Type: text/html; charset=UTF-8\r\n".getBytes());
                    out.write(("Content-Length: " + htmlBody.getBytes().length + "\r\n").getBytes());
                    out.write("\r\n".getBytes());
                    out.write(htmlBody.getBytes());
                    out.flush();
                    System.out.println("Response sent successfully");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT + e.getMessage());
        }
    }
}
