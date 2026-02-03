package com.velocity.ride.model.dto;

import java.math.BigDecimal;

import com.velocity.core.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for fare estimation before booking
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareEstimateDto {
    
    private VehicleType vehicleType;
    private BigDecimal distanceKm;
    private Integer estimatedDurationMinutes;
    
    // Fare breakdown
    private BigDecimal baseFare;
    private BigDecimal distanceFare;
    private BigDecimal timeFare;
    private BigDecimal surgeFare; // During peak hours
    private BigDecimal totalFare;
    
    // Additional info
    private BigDecimal surgeMultiplier; // 1.0 = no surge, 1.5 = 50% surge
    private String surgeReason; // "High demand", "Peak hours", etc.
    private Integer estimatedArrivalMinutes; // How long until driver arrives
}
