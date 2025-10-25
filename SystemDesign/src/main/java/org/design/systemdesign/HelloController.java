package org.design.systemdesign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;

@RestController
public class HelloController {  // Changed to public class for clarity

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Value("${server.port:8080}")
    private String port;

    @GetMapping("/hello")
    public String hello() {
        String host = "unknown";
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {}

        logger.info("Received /hello request on port {} (host: {})", port, host);  // Logs to console

        return "Hello from instance on port " + port + " (host: " + host + ")";
    }
}