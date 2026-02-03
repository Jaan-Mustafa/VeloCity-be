package com.velocity.ride.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.velocity.core.enums.RideStatus;
import com.velocity.core.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for ride response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideResponseDto {
    
    private Long id;
    private Long userId;
    private Long driverId;
    
    // Pickup location
    private BigDecimal pickupLatitude;
    private BigDecimal pickupLongitude;
    private String pickupAddress;
    
    // Dropoff location
    private BigDecimal dropoffLatitude;
    private BigDecimal dropoffLongitude;
    private String dropoffAddress;
    
    // Ride details
    private RideStatus status;
    private VehicleType vehicleType;
    
    // Pricing and metrics
    private BigDecimal fare;
    private BigDecimal distanceKm;
    private Integer durationMinutes;
    
    // Driver info (when assigned)
    private DriverInfoDto driver;
    
    // Timestamps
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime arrivedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    
    // Ratings (after completion)
    private BigDecimal riderRating;
    private BigDecimal driverRating;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverInfoDto {
        private Long id;
        private String name;
        private String phone;
        private BigDecimal rating;
        private String vehicleModel;
        private String vehicleNumber;
        private String profileImageUrl;
    }
}
