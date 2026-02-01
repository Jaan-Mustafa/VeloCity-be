package com.velocity.core.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Data Transfer Object for location information.
 * Contains coordinates and optional address details.
 * 
 * @author VeloCity Team
 * @version 1.0
 * @since 2026-02-01
 */
@Data
@Accessors(chain = true)
public class LocationDto {
    
    /**
     * Latitude coordinate
     * Valid range: -90 to +90
     */
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;
    
    /**
     * Longitude coordinate
     * Valid range: -180 to +180
     */
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;
    
    /**
     * Human-readable address (optional)
     * Example: "123 Main St, Mumbai, Maharashtra 400001"
     */
    private String address;
    
    /**
     * Creates a LocationDto with coordinates only
     * 
     * @param latitude Latitude
     * @param longitude Longitude
     * @return LocationDto instance
     */
    public static LocationDto of(Double latitude, Double longitude) {
        return new LocationDto()
                .setLatitude(latitude)
                .setLongitude(longitude);
    }
    
    /**
     * Creates a LocationDto with coordinates and address
     * 
     * @param latitude Latitude
     * @param longitude Longitude
     * @param address Address string
     * @return LocationDto instance
     */
    public static LocationDto of(Double latitude, Double longitude, String address) {
        return new LocationDto()
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setAddress(address);
    }
    
    /**
     * Checks if coordinates are valid
     * 
     * @return true if both latitude and longitude are within valid ranges
     */
    public boolean hasValidCoordinates() {
        return latitude != null 
                && longitude != null
                && latitude >= -90.0 && latitude <= 90.0
                && longitude >= -180.0 && longitude <= 180.0;
    }
}
