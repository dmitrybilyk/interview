package org.design.designurlshortenergenerator.configuration;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LoggingConfig {

    @Bean
    LoggingMeterRegistry loggingMeterRegistry() {
        return new LoggingMeterRegistry(new LoggingRegistryConfig() {
            @Override
            public String get(String key) { return null; }
            @Override
            public Duration step() { return Duration.ofSeconds(5); } // Print every 5s
        }, Clock.SYSTEM);
    }

    @Bean
    public MeterFilter onlyMyMetricsFilter() {
        return new MeterFilter() {
            @Override
            public MeterFilterReply accept(Meter.Id id) {
                // Only allow our specific counter to be logged
                if (id.getName().equals("url.shorten.shared.counter")) {
                    return MeterFilterReply.ACCEPT;
                }
                // Deny everything else from the logging registry
                return MeterFilterReply.DENY;
            }
        };
    }
}
