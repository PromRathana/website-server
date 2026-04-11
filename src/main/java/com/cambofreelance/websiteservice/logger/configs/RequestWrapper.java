package com.cambofreelance.websiteservice.logger.configs;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Set;

@Getter
public class RequestWrapper extends HttpServletRequestWrapper {

    private final String body;
    private String params;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        if (!request.getParameterMap().isEmpty()) {
            Set<String> keys = request.getParameterMap().keySet();
            StringBuilder queryString = new StringBuilder();

            for (String key : keys) {
                queryString.append(String.format("%s:%s", key,
                    Arrays.toString(request.getParameterMap().get(key)))).append(",");
            }

            this.params = queryString.toString();
        }

        this.body = getBodyRequest(request);
    }

    private static String getBodyRequest(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader reader = request.getReader();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append('\n');
                }
            } catch (Throwable var6) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }
                }

                throw var6;
            }

            reader.close();
        } catch (IllegalStateException var7) {
            stringBuilder.append("file data...");
        }

        return stringBuilder.toString();
    }

    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
            this.body.getBytes());
        return new ServletInputStream() {
            public boolean isFinished() {
                return false;
            }

            public boolean isReady() {
                return false;
            }

            public void setReadListener(ReadListener listener) {
            }

            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

}

