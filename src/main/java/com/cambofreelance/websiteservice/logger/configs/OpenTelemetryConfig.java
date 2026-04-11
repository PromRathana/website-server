package com.cambofreelance.websiteservice.logger.configs;


import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public Tracer tracer() {
        return GlobalOpenTelemetry.getTracer("com.cambofreelance.keycloakauthenticationserver");
    }
}
