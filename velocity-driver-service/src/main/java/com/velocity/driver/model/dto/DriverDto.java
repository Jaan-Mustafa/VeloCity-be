package com.velocity.driver.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.velocity.core.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {
    private Long id;
    private Long userId;
    private String licenseNumber;
    private VehicleType vehicleType;
    private String vehicleModel;
    private String vehicleNumber;
    private Boolean isAvailable;
    private BigDecimal rating;
    private Integer totalRides;
    private LocalDateTime createdAt;
    
    // Location information (if available)
    private Double latitude;
    private Double longitude;
    private LocalDateTime locationUpdatedAt;
}
