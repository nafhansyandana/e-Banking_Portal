package com.example.transactions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

// For DEBUG purpose
@Configuration
public class BearerLoggingConfig {

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return request -> {
            String header = request.getHeader("Authorization");
            System.out.println("DEBUG resolver: Authorization header = " + header);
            DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();
            String token = delegate.resolve(request);
            System.out.println("DEBUG resolver: resolved token = " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : null));
            return token;
        };
    }
}
