package com.velocity.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Simple Data Transfer Object for geographic coordinates.
 * Represents a latitude/longitude pair without additional metadata.
 * 
 * @author VeloCity Team
 * @version 1.0
 * @since 2026-02-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CoordinatesDto {
    
    /**
     * Latitude coordinate (-90 to +90)
     */
    private Double latitude;
    
    /**
     * Longitude coordinate (-180 to +180)
     */
    private Double longitude;
    
    /**
     * Creates a CoordinatesDto from latitude and longitude
     * 
     * @param latitude Latitude value
     * @param longitude Longitude value
     * @return CoordinatesDto instance
     */
    public static CoordinatesDto of(Double latitude, Double longitude) {
        return new CoordinatesDto(latitude, longitude);
    }
    
    /**
     * Checks if coordinates are valid
     * 
     * @return true if both coordinates are non-null and within valid ranges
     */
    public boolean isValid() {
        return latitude != null 
                && longitude != null
                && latitude >= -90.0 && latitude <= 90.0
                && longitude >= -180.0 && longitude <= 180.0;
    }
    
    /**
     * Converts to LocationDto
     * 
     * @return LocationDto with same coordinates
     */
    public LocationDto toLocationDto() {
        return LocationDto.of(latitude, longitude);
    }
}
