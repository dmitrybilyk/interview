package org.design.designshortenerdiscoveryeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DesignShortenerDiscoveryEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesignShortenerDiscoveryEurekaApplication.class, args);
    }

}
