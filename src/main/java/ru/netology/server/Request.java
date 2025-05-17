package ru.netology.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;

    public Request(InputStream inputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new Exception("Empty request line");
        }

        String[] parts = requestLine.split(" ");
        method = parts[0];
        path = parts[1];

        headers = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            headers.put(headerParts[0], headerParts[1]);
        }

        int contentLength = headers.getOrDefault("Content-Length", "0").isEmpty() ? 0
                : Integer.parseInt(headers.get("Content-Length"));
        char[] bodyChars = new char[contentLength];
        reader.read(bodyChars);
        body = new String(bodyChars);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
