package com.velocity.ride.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverBasicInfo {
    private Long driverId;
    private String name;
    private String phone;
    private String vehicleType;
    private String vehicleModel;
    private String vehicleNumber;
    private Double rating;
}
