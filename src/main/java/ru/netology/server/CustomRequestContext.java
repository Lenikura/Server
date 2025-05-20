package ru.netology.server;

import org.apache.commons.fileupload.RequestContext;

import java.io.IOException;
import java.io.InputStream;

public class CustomRequestContext implements RequestContext {
    private final InputStream inputStream;
    private final String contentType;
    private final int contentLength;

    public CustomRequestContext(InputStream inputStream, String contentType, int contentLength) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public String getCharacterEncoding() {
        return "UTF-8";
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }
}