package com.velocity.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Payment Service.
 * Handles wallet management, transactions, and payment processing.
 * 
 * Following rules.md:
 * - @SpringBootApplication for auto-configuration
 * - @EnableJpaAuditing for automatic timestamp management
 * - Component scanning enabled by default
 */
@SpringBootApplication(scanBasePackages = {
    "com.velocity.payment",
    "com.velocity.core"
})
@EnableJpaAuditing
public class PaymentServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
