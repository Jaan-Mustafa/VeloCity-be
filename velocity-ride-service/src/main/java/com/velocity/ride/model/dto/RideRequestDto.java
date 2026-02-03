package com.velocity.ride.model.dto;

import java.math.BigDecimal;

import com.velocity.core.enums.VehicleType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for ride request from user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {
    
    @NotNull(message = "Pickup latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal pickupLatitude;
    
    @NotNull(message = "Pickup longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal pickupLongitude;
    
    private String pickupAddress;
    
    @NotNull(message = "Dropoff latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal dropoffLatitude;
    
    @NotNull(message = "Dropoff longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal dropoffLongitude;
    
    private String dropoffAddress;
    
    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;
    
    private String notes; // Special instructions for driver
}
