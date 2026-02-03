package com.velocity.driver.mapper;

import org.springframework.stereotype.Component;

import com.velocity.driver.model.dto.DriverDto;
import com.velocity.driver.model.dto.NearbyDriverResponse;
import com.velocity.driver.model.entity.Driver;
import com.velocity.driver.model.entity.DriverLocation;

@Component
public class DriverMapper {
    
    public DriverDto toDto(Driver driver) {
        return DriverDto.builder()
                .id(driver.getId())
                .userId(driver.getUserId())
                .licenseNumber(driver.getLicenseNumber())
                .vehicleType(driver.getVehicleType())
                .vehicleModel(driver.getVehicleModel())
                .vehicleNumber(driver.getVehicleNumber())
                .isAvailable(driver.getIsAvailable())
                .rating(driver.getRating())
                .totalRides(driver.getTotalRides())
                .createdAt(driver.getCreatedAt())
                .build();
    }
    
    public DriverDto toDtoWithLocation(Driver driver, DriverLocation location) {
        DriverDto dto = toDto(driver);
        if (location != null) {
            dto.setLatitude(location.getLatitude().doubleValue());
            dto.setLongitude(location.getLongitude().doubleValue());
            dto.setLocationUpdatedAt(location.getUpdatedAt());
        }
        return dto;
    }
    
    public NearbyDriverResponse toNearbyDriverResponse(Driver driver, Double latitude, Double longitude, Double distanceKm) {
        return NearbyDriverResponse.builder()
                .driverId(driver.getId())
                .vehicleModel(driver.getVehicleModel())
                .vehicleNumber(driver.getVehicleNumber())
                .vehicleType(driver.getVehicleType().name())
                .rating(driver.getRating())
                .totalRides(driver.getTotalRides())
                .latitude(latitude)
                .longitude(longitude)
                .distanceKm(distanceKm)
                .build();
    }
}
