package com.velocity.core.exception;

import com.velocity.core.constants.ErrorCodes;

import lombok.Getter;

/**
 * Exception thrown when no drivers are available for a ride request.
 * Includes location and search radius information for debugging.
 * 
 * @author VeloCity Team
 * @since 1.0.0
 */
@Getter
public class DriverNotAvailableException extends BusinessException {
    
    private final Double latitude;
    private final Double longitude;
    private final Double radiusKm;
    
    /**
     * Constructor with location and radius.
     *
     * @param latitude the pickup latitude
     * @param longitude the pickup longitude
     * @param radiusKm the search radius in kilometers
     */
    public DriverNotAvailableException(Double latitude, Double longitude, Double radiusKm) {
        super(
            String.format(
                "No drivers available near location (%.6f, %.6f) within %.2f km radius",
                latitude,
                longitude,
                radiusKm
            ),
            ErrorCodes.DRIVER_NOT_AVAILABLE
        );
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKm = radiusKm;
    }
    
    /**
     * Constructor with custom message and location.
     *
     * @param message custom error message
     * @param latitude the pickup latitude
     * @param longitude the pickup longitude
     * @param radiusKm the search radius in kilometers
     */
    public DriverNotAvailableException(String message, Double latitude, Double longitude, Double radiusKm) {
        super(message, ErrorCodes.DRIVER_NOT_AVAILABLE);
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKm = radiusKm;
    }
    
    /**
     * Constructor with simple message (no location details).
     *
     * @param message custom error message
     */
    public DriverNotAvailableException(String message) {
        super(message, ErrorCodes.DRIVER_NOT_AVAILABLE);
        this.latitude = null;
        this.longitude = null;
        this.radiusKm = null;
    }
}
