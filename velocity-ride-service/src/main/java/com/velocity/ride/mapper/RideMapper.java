package com.velocity.ride.mapper;

import com.velocity.ride.model.dto.RideRequestDto;
import com.velocity.ride.model.dto.RideResponseDto;
import com.velocity.ride.model.entity.Ride;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Ride entity and DTOs
 */
@Mapper(componentModel = "spring")
public interface RideMapper {
    
    /**
     * Convert RideRequestDto to Ride entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "driverId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "fare", ignore = true)
    @Mapping(target = "distanceKm", ignore = true)
    @Mapping(target = "durationMinutes", ignore = true)
    @Mapping(target = "cancelledBy", ignore = true)
    @Mapping(target = "cancellationReason", ignore = true)
    @Mapping(target = "cancellationFee", ignore = true)
    @Mapping(target = "riderRating", ignore = true)
    @Mapping(target = "driverRating", ignore = true)
    @Mapping(target = "riderFeedback", ignore = true)
    @Mapping(target = "driverFeedback", ignore = true)
    @Mapping(target = "requestedAt", ignore = true)
    @Mapping(target = "acceptedAt", ignore = true)
    @Mapping(target = "arrivedAt", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Ride toEntity(RideRequestDto dto);
    
    /**
     * Convert Ride entity to RideResponseDto
     */
    @Mapping(target = "driver", ignore = true) // Will be populated separately
    RideResponseDto toDto(Ride entity);
    
    /**
     * Update existing Ride entity from DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "driverId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(RideRequestDto dto, @MappingTarget Ride entity);
}
