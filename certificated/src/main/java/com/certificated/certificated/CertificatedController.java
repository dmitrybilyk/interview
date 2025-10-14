package com.certificated.certificated;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CertificatedController {

    @GetMapping
    public String certificated() {
        return "certificated";
    }

    @GetMapping("/api/hello")
    public String apiHello() {
        return "api hello";
    }
}
