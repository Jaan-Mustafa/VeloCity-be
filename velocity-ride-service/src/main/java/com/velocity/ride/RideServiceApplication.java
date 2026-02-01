package com.velocity.ride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Ride Service.
 * Handles ride requests, matching, and lifecycle management.
 * 
 * Following rules.md:
 * - @SpringBootApplication for auto-configuration
 * - @EnableJpaAuditing for automatic timestamp management
 * - Component scanning enabled by default
 */
@SpringBootApplication(scanBasePackages = {
    "com.velocity.ride",
    "com.velocity.core"
})
@EnableJpaAuditing
public class RideServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RideServiceApplication.class, args);
    }
}
