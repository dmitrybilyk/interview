package com.conduct.interview.practise.certified;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

public class SecureWebClientStandalone {

    public static void main(String[] args) throws Exception {

        // ───────────────────────────────────────────────
        // Load truststore (should contain only root CA / server certs you trust)
        // ───────────────────────────────────────────────
        KeyStore trustStore = KeyStore.getInstance("PKCS12");

        // Important: adjust path if needed
        try (InputStream trustStoreStream =
                     SecureWebClientStandalone.class.getResourceAsStream("/client-truststore-new.p12")) {

            if (trustStoreStream == null) {
                throw new IllegalStateException("Truststore file not found: /client-truststore-new.p12");
            }

            trustStore.load(trustStoreStream, "changeit".toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // ───────────────────────────────────────────────
        // Build Netty SSL context — only trust managers, no client cert
        // ───────────────────────────────────────────────
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(tmf)
                .build();

        // ───────────────────────────────────────────────
        // Create secure Reactor Netty HttpClient
        // ───────────────────────────────────────────────
        HttpClient httpClient = HttpClient.create()
                .secure(spec -> spec.sslContext(sslContext));

        // ───────────────────────────────────────────────
        // Build WebClient
        // ───────────────────────────────────────────────
        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://localhost:8092/api")
                .build();

        // ───────────────────────────────────────────────
        // Quick test — just to see it starts without SSL exception
        // ───────────────────────────────────────────────
        System.out.println("WebClient created successfully.");
        System.out.println("Base URL: " + webClient.get().uri("").toString());

        String response = webClient.get()
                .uri("/with/certificate")   // ← your endpoint
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> System.err.println("Request failed: " + e.getMessage()))
                .block();   // only using .block() because we're in main()

        System.out.println("───────────────────────────────");
        System.out.println("Response from server:");
        System.out.println(response != null ? response : "(null response)");
        System.out.println("───────────────────────────────");
    }
}