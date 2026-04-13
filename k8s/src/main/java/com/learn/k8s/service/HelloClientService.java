package com.learn.k8s.service;

import com.learn.k8s.dto.HelloRequest;
import com.learn.k8s.dto.HelloResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class HelloClientService {

    private final WebClient webClient;

    public HelloClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<HelloResponse> sendHello() {
        HelloRequest myRequest = new HelloRequest("Dmytro", "learning_k8s");

        return webClient.post()
                .uri("/hello")
                .bodyValue(myRequest)
                .retrieve()
                .bodyToMono(HelloResponse.class);
    }
}