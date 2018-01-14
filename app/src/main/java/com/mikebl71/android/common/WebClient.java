package com.mikebl71.android.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Client for retrieving content of a Web resource.
 */
public class WebClient {

    public ResourceContent retrieve(String url) {
        return retrieve(url, null, null, null);
    }

    public ResourceContent retrieve(String url, Map<String,String> headers, String from, String to) {
        ResourceContent content;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            if (headers != null) {
                for (Map.Entry<String,String> header : headers.entrySet()) {
                    urlConnection.setRequestProperty(header.getKey(), header.getValue());
                }
            }

            String contentType = urlConnection.getContentType();
            if (contentType != null && contentType.startsWith("image")) {
                content = new ResourceContent(convertToByteArray(urlConnection));
            } else {
                content = new ResourceContent(convertToString(urlConnection, from, to));
            }

        } catch (Exception ex) {
            content = new ResourceContent(ex);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return content;
    }

    private String convertToString(final HttpURLConnection urlConnection, final String from, final String to) throws IOException {
        InputStream inStream = urlConnection.getInputStream();
        if (inStream == null) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, getContentCharset(urlConnection)));
        StringBuilder sb = new StringBuilder();
        try {
            boolean isFragmentStarted = (from == null);
            boolean isFragmentEnded = false;

            String line;
            while ((line = reader.readLine()) != null) {
                if (!isFragmentStarted && from != null) {
                    int startPos = line.indexOf(from);
                    if (startPos != -1) {
                        line = line.substring(startPos);
                        isFragmentStarted = true;
                    }
                }

                boolean isInFragment = (isFragmentStarted && !isFragmentEnded);

                if (isFragmentStarted && !isFragmentEnded && to != null) {
                    int endPos = line.indexOf(to);
                    if (endPos != -1) {
                        line = line.substring(0, endPos);
                        isFragmentEnded = true;
                    }
                }

                if (isInFragment) {
                    sb.append(line);
                }
            }
        } finally {
            reader.close();
        }

        return sb.toString();
    }

    private Charset getContentCharset(final HttpURLConnection urlConnection) {
        try {
            String contentType = urlConnection.getContentType();
            if (contentType != null && contentType.contains("charset=")) {
                return Charset.forName(contentType.substring(contentType.indexOf("charset=")+8));
            }
        } catch (Exception ex) {
        }
        return StandardCharsets.UTF_8;
    }

    private static byte[] convertToByteArray(final HttpURLConnection urlConnection) throws IOException {
        InputStream inStream = urlConnection.getInputStream();
        if (inStream == null) {
            return null;
        }
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int bytesRead;
            while ((bytesRead = inStream.read(chunk)) != -1) {
                buffer.write(chunk, 0, bytesRead);
            }
            return buffer.toByteArray();
        } finally {
            inStream.close();
        }
    }

}
