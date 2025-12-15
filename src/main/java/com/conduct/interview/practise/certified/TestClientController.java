package com.conduct.interview.practise.certified;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestClientController {

    @Autowired
    private CertificateApiClient certificateApiClient;

    @GetMapping("/test/api/with/certificate")
    public String testClient() {
        return certificateApiClient.callHello();
    }
}
