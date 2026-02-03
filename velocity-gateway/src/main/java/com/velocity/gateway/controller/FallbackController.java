package com.velocity.gateway.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        return buildFallbackResponse("User Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/drivers")
    public ResponseEntity<Map<String, Object>> driverServiceFallback() {
        return buildFallbackResponse("Driver Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/rides")
    public ResponseEntity<Map<String, Object>> rideServiceFallback() {
        return buildFallbackResponse("Ride Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/payments")
    public ResponseEntity<Map<String, Object>> paymentServiceFallback() {
        return buildFallbackResponse("Payment Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        return buildFallbackResponse("Notification Service is currently unavailable. Please try again later.");
    }

    private ResponseEntity<Map<String, Object>> buildFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "SERVICE_UNAVAILABLE");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
