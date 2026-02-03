package com.velocity.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    /**
     * Rate limiter key resolver based on user IP address
     * Can be enhanced to use user ID from JWT token
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Try to get user ID from header (set by JWT filter)
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null) {
                return Mono.just(userId);
            }
            
            // Fallback to IP address
            String ip = exchange.getRequest().getRemoteAddress() != null 
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
            return Mono.just(ip);
        };
    }
}
