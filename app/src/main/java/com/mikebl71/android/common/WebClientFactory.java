package com.mikebl71.android.common;

/**
 * Factory for Web Clients.
 */
public class WebClientFactory {

    private final static WebClient webClient = new WebClient();

    public static WebClient getClient() {
        return webClient;
    }
}
