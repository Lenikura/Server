package ru.netology.server;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();

        server.addHandler("GET", "/messages", (request, outputStream) -> {
            String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: 7\r\n\r\nMessage";
            try {
                outputStream.write(response.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("POST", "/messages", (request, outputStream) -> {
            String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: 4\r\n\r\nPost";
            try {
                outputStream.write(response.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.listen(9999);
    }
}