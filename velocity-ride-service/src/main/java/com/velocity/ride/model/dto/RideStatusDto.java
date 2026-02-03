package com.velocity.ride.model.dto;

import com.velocity.core.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideStatusDto {
    private Long rideId;
    private RideStatus status;
    private String statusMessage;
    private LocalDateTime lastUpdated;
    private DriverBasicInfo driver;
    private Integer estimatedArrivalMinutes;
    private LocationInfo pickup;
    private LocationInfo dropoff;
}
