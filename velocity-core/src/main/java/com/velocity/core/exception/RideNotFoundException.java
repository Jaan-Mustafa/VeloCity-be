package com.velocity.core.exception;

/**
 * Exception thrown when a ride is not found.
 * Extends ResourceNotFoundException with ride-specific context.
 * 
 * @author VeloCity Team
 * @since 1.0.0
 */
public class RideNotFoundException extends ResourceNotFoundException {
    
    /**
     * Constructor with ride ID.
     *
     * @param rideId the ID of the ride that was not found
     */
    public RideNotFoundException(Long rideId) {
        super("Ride", "id", rideId);
    }
    
    /**
     * Constructor with custom message.
     *
     * @param message custom error message
     */
    public RideNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructor with field and value.
     *
     * @param field the field used for lookup
     * @param value the value that was not found
     */
    public RideNotFoundException(String field, Object value) {
        super("Ride", field, value);
    }
}
