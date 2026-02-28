package com.learn.k8s.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.k8s.dto.HelloRequest;
import com.learn.k8s.dto.HelloResponse;
import com.learn.k8s.dto.UserResponse;
import com.learn.k8s.service.HelloClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HelloController {

    private final ObjectMapper objectMapper;
    private final HelloClientService helloClientService;

    public HelloController(ObjectMapper objectMapper, HelloClientService helloClientService) {
        this.objectMapper = objectMapper;
        this.helloClientService = helloClientService;
    }

    @GetMapping("/user")
    public Mono<UserResponse> getUser() {
        UserResponse user = new UserResponse(
                1L,
                "Dmytro",
                "dima@example.com",
                LocalDateTime.now()
        );
        return Mono.just(user);
    }

    @PostMapping("/hello")
    public Mono<HelloResponse> hello(@RequestBody HelloRequest request) {
        System.out.println("Отримано запит для: " + request.name());

        String responseMessage = "Привіт, " + request.name() + "! Дія: " + request.actionType();
        return Mono.just(new HelloResponse(responseMessage, "SUCCESS"));
    }

    @GetMapping("/hello/client")
    public Mono<HelloResponse> helloClient() {
        return helloClientService.sendHello();
    }

    @GetMapping("/debug/mapper")
    public Mono<Map<String, Object>> debugMapper() {
        return Mono.just(Map.of(
                "beanPresent", objectMapper != null,
                "className", objectMapper.getClass().getName(),
                "registeredModules", objectMapper.getRegisteredModuleIds()
        ));
    }
}
