package ru.netology.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

public class Server {
    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);

    public void addHandler(String method, String path, Handler handler) {
        handlers.computeIfAbsent(method, k -> new ConcurrentHashMap<>()).put(path, handler);
    }

    public void listen(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try (socket;
             var input = socket.getInputStream();
             var output = new BufferedOutputStream(socket.getOutputStream())) {

            Request request = new Request(input);
            Handler handler = handlers.getOrDefault(request.getMethod(), Map.of()).get(request.getPath());

            if (handler != null) {
                handler.handle(request, output);
            } else {
                output.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "\r\n").getBytes());
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}