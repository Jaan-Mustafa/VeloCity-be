package com.velocity.driver.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriverResponse {
    private Long driverId;
    private String vehicleModel;
    private String vehicleNumber;
    private String vehicleType;
    private BigDecimal rating;
    private Integer totalRides;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;
}
