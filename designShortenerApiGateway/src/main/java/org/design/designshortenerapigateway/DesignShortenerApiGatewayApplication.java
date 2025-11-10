package org.design.designshortenerapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class DesignShortenerApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesignShortenerApiGatewayApplication.class, args);
    }

    @Bean(name = "ipKeyResolver")
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            var address = exchange.getRequest().getRemoteAddress();
            if (address == null) {
                return Mono.just("unknown");
            }
            return Mono.just(address.getAddress().getHostAddress());
        };
    }

}
