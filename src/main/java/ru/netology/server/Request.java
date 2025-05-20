package ru.netology.server;

import org.apache.commons.fileupload.FileItem;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;

    private final Map<String, List<String>> queryParams = new HashMap<>();
    private final Map<String, List<String>> postParams = new HashMap<>();
    private final Map<String, FileItem> multipartParams = new HashMap<>();

    public Request(InputStream inputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) throw new Exception("Empty request line");

        String[] parts = requestLine.split(" ");
        method = parts[0];

        String fullPath = parts[1];
        String[] pathParts = fullPath.split("\\?", 2);
        path = pathParts[0];

        if (pathParts.length > 1) {
            List<NameValuePair> params = URLEncodedUtils.parse(pathParts[1], StandardCharsets.UTF_8);
            for (NameValuePair param : params) {
                queryParams.computeIfAbsent(param.getName(), k -> new ArrayList<>()).add(param.getValue());
            }
        }

        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] header = line.split(": ", 2);
            if (header.length == 2) {
                headers.put(header[0], header[1]);
            }
        }

        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        char[] bodyChars = new char[contentLength];
        reader.read(bodyChars);
        body = new String(bodyChars);

        String contentType = headers.getOrDefault("Content-Type", "");

        if ("application/x-www-form-urlencoded".equalsIgnoreCase(contentType)) {
            List<NameValuePair> params = URLEncodedUtils.parse(body, StandardCharsets.UTF_8);
            for (NameValuePair param : params) {
                postParams.computeIfAbsent(param.getName(), k -> new ArrayList<>()).add(param.getValue());
            }
        } else if (contentType.startsWith("multipart/form-data")) {
            processMultipart(body, contentType, contentLength);
        }
    }

    private void processMultipart(String body, String contentType, int contentLength) throws IOException {
        String boundary = contentType.split("boundary=")[1];
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        MultipartParser multipartParser = new MultipartParser(byteArrayInputStream, boundary);

        List<FileItem> items = multipartParser.parse();
        for (FileItem item : items) {
            if (item.isFormField()) {
                postParams.computeIfAbsent(item.getFieldName(), k -> new ArrayList<>()).add(item.getString());
            } else {
                multipartParams.put(item.getFieldName(), item);
            }
        }
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

    public String getQueryParam(String name) {
        var list = queryParams.get(name);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public String getPostParam(String name) {
        var list = postParams.get(name);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    public Map<String, List<String>> getPostParams() {
        return postParams;
    }

    public FileItem getPart(String name) {
        return multipartParams.get(name);
    }

    public Map<String, FileItem> getParts() {
        return multipartParams;
    }
}