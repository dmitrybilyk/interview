package com.learn.k8s.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig implements WebFluxConfigurer {

    private final Jackson2JsonEncoder encoder;
    private final ObjectMapper commonMapper;

    public WebConfig(@Qualifier("serverUppercaseEncoder") Jackson2JsonEncoder encoder, ObjectMapper commonMapper) {
        this.encoder = encoder;
        this.commonMapper = commonMapper;
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(encoder);
    }

    @Bean
    public WebClient webClient() {
        Jackson2JsonEncoder standardEncoder = new Jackson2JsonEncoder(commonMapper);
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonEncoder(standardEncoder);
                })
                .build();

        return WebClient.builder()
                .exchangeStrategies(strategies)
                .baseUrl("http://localhost:8080")
                .build();
    }
}