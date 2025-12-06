package com.conduct.interview.practise.certified;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CertificateApiClient {

    private final WebClient webClient;

    public CertificateApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public String callHello() {
        return webClient.get()
                .uri("/with/certificate")
                .retrieve()
                .bodyToMono(String.class)
                .block(); // blocking for simplicity
    }
}
