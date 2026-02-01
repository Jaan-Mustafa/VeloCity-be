package com.velocity.driver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Driver Service.
 * Handles driver registration, availability, and location tracking.
 * 
 * Following rules.md:
 * - @SpringBootApplication for auto-configuration
 * - @EnableJpaAuditing for automatic timestamp management
 * - Component scanning enabled by default
 */
@SpringBootApplication(scanBasePackages = {
    "com.velocity.driver",
    "com.velocity.core"
})
@EnableJpaAuditing
public class DriverServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DriverServiceApplication.class, args);
    }
}
