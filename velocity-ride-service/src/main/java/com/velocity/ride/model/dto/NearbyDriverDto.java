package com.velocity.ride.model.dto;

import java.math.BigDecimal;

import com.velocity.core.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for nearby driver information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriverDto {
    
    private Long driverId;
    private String name;
    private BigDecimal rating;
    private Integer totalRides;
    
    // Location
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal distanceKm; // Distance from pickup point
    
    // Vehicle
    private VehicleType vehicleType;
    private String vehicleModel;
    private String vehicleNumber;
    private String vehicleColor;
    
    // Availability
    private Boolean isAvailable;
    private Integer estimatedArrivalMinutes;
}
