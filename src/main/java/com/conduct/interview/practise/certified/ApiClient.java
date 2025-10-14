package com.conduct.interview.practise.certified;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ApiClient {

    private final WebClient webClient;

    public ApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public String callHello() {
        return webClient.get()
                .uri("/hello")
                .retrieve()
                .bodyToMono(String.class)
                .block(); // blocking for simplicity
    }
}
