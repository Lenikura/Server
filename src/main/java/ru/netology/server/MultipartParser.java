package ru.netology.server;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MultipartParser {
    private final InputStream inputStream;
    private final String boundary;

    public MultipartParser(InputStream inputStream, String boundary) {
        this.inputStream = inputStream;
        this.boundary = boundary;
    }

    public List<FileItem> parse() throws IOException {
        List<FileItem> items = new ArrayList<>();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.UTF_8);
        byte[] endBoundaryBytes = ("--" + boundary + "--").getBytes(StandardCharsets.UTF_8);

        int nextByte;
        while ((nextByte = inputStream.read()) != -1) {
            outputStream.write(nextByte);
            byte[] data = outputStream.toByteArray();

            if (data.length >= boundaryBytes.length && endsWith(data, boundaryBytes)) {
                String contentDisposition = readContentDisposition(outputStream);
                String contentType = readContentType(outputStream);

                FileItem item = new DiskFileItemFactory().createItem(contentDisposition, contentType, true, "");
                items.add(item);

                outputStream.reset();
            }

            if (endsWith(data, endBoundaryBytes)) {
                break;
            }
        }

        return items;
    }

    private boolean endsWith(byte[] data, byte[] suffix) {
        if (data.length < suffix.length) {
            return false;
        }
        for (int i = 0; i < suffix.length; i++) {
            if (data[data.length - suffix.length + i] != suffix[i]) {
                return false;
            }
        }
        return true;
    }

    private String readContentDisposition(ByteArrayOutputStream outputStream) throws IOException {
        return "form-data";
    }

    private String readContentType(ByteArrayOutputStream outputStream) throws IOException {
        return "application/octet-stream";
    }
}