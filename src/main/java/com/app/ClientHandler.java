package com.app;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (Socket socket = this.clientSocket) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String requestLine = in.readLine();

            if (requestLine == null) return;

            System.out.println("[" + Thread.currentThread() + "] Zone 1 (Request Line): " + requestLine);

            String[] zone1Parts = requestLine.split(" ");
            String method = zone1Parts[0];
            URI uri = URI.create(zone1Parts[1]);
            String path = uri.getPath();
            Map<String, String> params = parseQueryString(uri.getRawQuery());

            String headerLine;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                System.out.println("[" + Thread.currentThread() + "] Zone 2 (Header Line): " + headerLine);
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
                case "/greet" -> {
                    statusLine = "HTTP/1.1 200 OK";
                    String name = params.getOrDefault("name", "");
                    htmlBody = "<h1>Hello " + name + "!</h1>";
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
            System.out.println("[" + Thread.currentThread() + "] Response sent successfully");
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();

        if (query == null || query.isBlank()) {
            return params;
        }

        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);

            if (keyValue.length > 1) {
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            } else {
                params.put(key, "");
            }
        }

        return params;
    }
}
