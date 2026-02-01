package com.velocity.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for User Service.
 * Handles user registration, authentication, and profile management.
 * 
 * Following rules.md:
 * - @SpringBootApplication for auto-configuration
 * - @EnableJpaAuditing for automatic timestamp management
 * - Component scanning enabled by default
 */
@SpringBootApplication(scanBasePackages = {
    "com.velocity.user",
    "com.velocity.core"
})
@EnableJpaAuditing
public class UserServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
