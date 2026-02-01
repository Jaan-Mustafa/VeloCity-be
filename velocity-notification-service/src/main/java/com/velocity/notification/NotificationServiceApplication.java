package com.velocity.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for Notification Service.
 * Handles multi-channel notifications (EMAIL, SMS, PUSH, IN_APP).
 * 
 * Following rules.md:
 * - @SpringBootApplication for auto-configuration
 * - @EnableAsync for asynchronous notification sending
 * - Component scanning enabled by default
 */
@SpringBootApplication(scanBasePackages = {
    "com.velocity.notification",
    "com.velocity.core"
})
@EnableAsync
public class NotificationServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
