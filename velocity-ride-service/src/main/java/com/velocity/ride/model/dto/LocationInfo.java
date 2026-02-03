package com.velocity.ride.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationInfo {
    private Double latitude;
    private Double longitude;
    private String address;
}
