package com.velocity.core.enums;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Vehicle types available in the VeloCity system.
 * This is a rich enum that encapsulates pricing logic and vehicle characteristics.
 *
 * Each vehicle type has:
 * - Base fare (charged at the start of the ride)
 * - Per kilometer rate
 * - Per minute rate
 * - Passenger capacity
 */
@Getter
public enum VehicleType {
    
    BIKE(
        "Bike",
        new BigDecimal("10.00"),  // Base fare
        new BigDecimal("8.00"),   // Per km rate
        new BigDecimal("1.00"),   // Per minute rate
        1                          // Capacity
    ),
    
    AUTO(
        "Auto Rickshaw",
        new BigDecimal("20.00"),
        new BigDecimal("12.00"),
        new BigDecimal("1.50"),
        3
    ),
    
    MINI(
        "Mini",
        new BigDecimal("30.00"),
        new BigDecimal("15.00"),
        new BigDecimal("2.00"),
        4
    ),
    
    SEDAN(
        "Sedan",
        new BigDecimal("40.00"),
        new BigDecimal("18.00"),
        new BigDecimal("2.50"),
        4
    ),
    
    SUV(
        "SUV",
        new BigDecimal("50.00"),
        new BigDecimal("22.00"),
        new BigDecimal("3.00"),
        6
    );
    
    private final String displayName;
    private final BigDecimal baseFare;
    private final BigDecimal perKmRate;
    private final BigDecimal perMinuteRate;
    private final int capacity;
    
    VehicleType(String displayName, BigDecimal baseFare, BigDecimal perKmRate,
                BigDecimal perMinuteRate, int capacity) {
        this.displayName = displayName;
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.perMinuteRate = perMinuteRate;
        this.capacity = capacity;
    }
    
    /**
     * Calculate the total fare for a ride based on distance and duration.
     * Formula: Base Fare + (Distance × Per Km Rate) + (Duration × Per Minute Rate)
     * 
     * @param distanceKm the distance traveled in kilometers
     * @param durationMinutes the duration of the ride in minutes
     * @return the calculated fare rounded to 2 decimal places
     */
    public BigDecimal calculateFare(double distanceKm, int durationMinutes) {
        BigDecimal distanceFare = BigDecimal.valueOf(distanceKm)
            .multiply(perKmRate);
        
        BigDecimal timeFare = BigDecimal.valueOf(durationMinutes)
            .multiply(perMinuteRate);
        
        return baseFare
            .add(distanceFare)
            .add(timeFare)
            .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate estimated fare for a given distance (assuming average speed).
     * Assumes average speed of 30 km/h for time estimation.
     * 
     * @param distanceKm the estimated distance in kilometers
     * @return the estimated fare
     */
    public BigDecimal estimateFare(double distanceKm) {
        // Assume average speed of 30 km/h
        int estimatedMinutes = (int) Math.ceil((distanceKm / 30.0) * 60);
        return calculateFare(distanceKm, estimatedMinutes);
    }
    
    /**
     * Get the minimum fare for this vehicle type.
     * Minimum fare is base fare + 1 km + 2 minutes.
     * 
     * @return the minimum fare
     */
    public BigDecimal getMinimumFare() {
        return calculateFare(1.0, 2);
    }
    
    /**
     * Check if this vehicle type can accommodate the given number of passengers.
     * 
     * @param passengerCount the number of passengers
     * @return true if the vehicle can accommodate the passengers
     */
    public boolean canAccommodate(int passengerCount) {
        return passengerCount > 0 && passengerCount <= capacity;
    }
}
