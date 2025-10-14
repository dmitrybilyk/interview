package com.conduct.interview.practise.certified;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestClientController {

    @Autowired
    private ApiClient apiClient;

    @GetMapping("/test/api/client")
    public String testClient() {
        return apiClient.callHello();
    }
}
