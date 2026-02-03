package com.velocity.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.velocity.core.enums.VehicleType;

/**
 * Utility class for calculating ride fares and prices.
 * Uses BigDecimal for precise monetary calculations to avoid floating-point errors.
 * 
 * <p>All monetary values are handled with 2 decimal places precision.</p>
 * 
 * <p>All methods are static and the class cannot be instantiated.</p>
 * 
 * @author VeloCity Team
 * @version 1.0
 * @since 2026-02-01
 */
public final class PriceCalculator {

    /**
     * Scale for monetary calculations (2 decimal places).
     */
    private static final int DECIMAL_SCALE = 2;

    /**
     * Rounding mode for monetary calculations (half up).
     */
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Platform fee percentage (5% of base fare).
     */
    private static final BigDecimal PLATFORM_FEE_PERCENTAGE = new BigDecimal("0.05");

    /**
     * GST percentage (18% in India).
     */
    private static final BigDecimal GST_PERCENTAGE = new BigDecimal("0.18");

    /**
     * Surge multiplier during normal hours (1.0x).
     */
    private static final BigDecimal NORMAL_SURGE = BigDecimal.ONE;

    /**
     * Surge multiplier during peak hours (1.5x).
     */
    private static final BigDecimal PEAK_SURGE = new BigDecimal("1.5");

    /**
     * Surge multiplier during high demand (2.0x).
     */
    private static final BigDecimal HIGH_DEMAND_SURGE = new BigDecimal("2.0");

    /**
     * Waiting charge per minute after free waiting time (₹2 per minute).
     */
    private static final BigDecimal WAITING_CHARGE_PER_MINUTE = new BigDecimal("2.00");

    /**
     * Free waiting time in minutes (5 minutes).
     */
    private static final int FREE_WAITING_MINUTES = 5;

    /**
     * Cancellation fee for rider (₹50).
     */
    private static final BigDecimal RIDER_CANCELLATION_FEE = new BigDecimal("50.00");

    /**
     * Cancellation fee for driver (₹100).
     */
    private static final BigDecimal DRIVER_CANCELLATION_FEE = new BigDecimal("100.00");

    /**
     * Minimum ride fare (₹30).
     */
    private static final BigDecimal MINIMUM_FARE = new BigDecimal("30.00");

    /**
     * Private constructor to prevent instantiation.
     * 
     * @throws UnsupportedOperationException if instantiation is attempted
     */
    private PriceCalculator() {
        throw new UnsupportedOperationException("PriceCalculator is a utility class and cannot be instantiated");
    }

    /**
     * Calculates the base fare for a ride using the VehicleType enum.
     * 
     * @param vehicleType the type of vehicle
     * @param distanceKm the distance in kilometers
     * @param durationMinutes the duration in minutes
     * @return the base fare
     * @throws IllegalArgumentException if any parameter is null or negative
     */
    public static BigDecimal calculateBaseFare(VehicleType vehicleType, double distanceKm, long durationMinutes) {
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        if (distanceKm < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        return vehicleType.calculateFare(distanceKm, (int) durationMinutes);
    }

    /**
     * Calculates the base fare for a ride with custom rates.
     * 
     * @param baseFare the base fare
     * @param perKmRate the rate per kilometer
     * @param perMinuteRate the rate per minute
     * @param distanceKm the distance in kilometers
     * @param durationMinutes the duration in minutes
     * @return the calculated base fare
     * @throws IllegalArgumentException if any parameter is negative
     */
    public static BigDecimal calculateBaseFare(
            BigDecimal baseFare,
            BigDecimal perKmRate,
            BigDecimal perMinuteRate,
            double distanceKm,
            long durationMinutes) {

        validatePositive(baseFare, "Base fare");
        validatePositive(perKmRate, "Per km rate");
        validatePositive(perMinuteRate, "Per minute rate");

        if (distanceKm < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        BigDecimal distanceCost = perKmRate.multiply(BigDecimal.valueOf(distanceKm));
        BigDecimal timeCost = perMinuteRate.multiply(BigDecimal.valueOf(durationMinutes));

        BigDecimal totalFare = baseFare.add(distanceCost).add(timeCost);

        return round(totalFare);
    }

    /**
     * Applies surge pricing to a base fare.
     * 
     * @param baseFare the base fare
     * @param surgeMultiplier the surge multiplier (e.g., 1.5 for 1.5x surge)
     * @return the fare with surge applied
     * @throws IllegalArgumentException if baseFare is null or negative, or surgeMultiplier is less than 1.0
     */
    public static BigDecimal applySurge(BigDecimal baseFare, BigDecimal surgeMultiplier) {
        validatePositive(baseFare, "Base fare");
        if (surgeMultiplier == null || surgeMultiplier.compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("Surge multiplier must be at least 1.0");
        }

        BigDecimal surgedFare = baseFare.multiply(surgeMultiplier);
        return round(surgedFare);
    }

    /**
     * Calculates platform fee (5% of base fare).
     * 
     * @param baseFare the base fare
     * @return the platform fee
     * @throws IllegalArgumentException if baseFare is null or negative
     */
    public static BigDecimal calculatePlatformFee(BigDecimal baseFare) {
        validatePositive(baseFare, "Base fare");
        BigDecimal fee = baseFare.multiply(PLATFORM_FEE_PERCENTAGE);
        return round(fee);
    }

    /**
     * Calculates GST (18% of subtotal).
     * 
     * @param subtotal the subtotal amount
     * @return the GST amount
     * @throws IllegalArgumentException if subtotal is null or negative
     */
    public static BigDecimal calculateGst(BigDecimal subtotal) {
        validatePositive(subtotal, "Subtotal");
        BigDecimal gst = subtotal.multiply(GST_PERCENTAGE);
        return round(gst);
    }

    /**
     * Calculates waiting charges based on waiting time.
     * First 5 minutes are free, then ₹2 per minute.
     * 
     * @param waitingMinutes the total waiting time in minutes
     * @return the waiting charges
     * @throws IllegalArgumentException if waitingMinutes is negative
     */
    public static BigDecimal calculateWaitingCharges(long waitingMinutes) {
        if (waitingMinutes < 0) {
            throw new IllegalArgumentException("Waiting minutes cannot be negative");
        }

        if (waitingMinutes <= FREE_WAITING_MINUTES) {
            return BigDecimal.ZERO;
        }

        long chargeableMinutes = waitingMinutes - FREE_WAITING_MINUTES;
        BigDecimal charges = WAITING_CHARGE_PER_MINUTE.multiply(BigDecimal.valueOf(chargeableMinutes));
        return round(charges);
    }

    /**
     * Calculates the total fare including all charges.
     * 
     * @param baseFare the base fare
     * @param surgeMultiplier the surge multiplier
     * @param waitingMinutes the waiting time in minutes
     * @return the total fare breakdown
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static FareBreakdown calculateTotalFare(
            BigDecimal baseFare,
            BigDecimal surgeMultiplier,
            long waitingMinutes) {

        validatePositive(baseFare, "Base fare");

        // Apply surge
        BigDecimal surgedFare = applySurge(baseFare, surgeMultiplier);

        // Calculate waiting charges
        BigDecimal waitingCharges = calculateWaitingCharges(waitingMinutes);

        // Calculate subtotal
        BigDecimal subtotal = surgedFare.add(waitingCharges);

        // Calculate platform fee
        BigDecimal platformFee = calculatePlatformFee(subtotal);

        // Calculate GST
        BigDecimal gst = calculateGst(subtotal.add(platformFee));

        // Calculate total
        BigDecimal total = subtotal.add(platformFee).add(gst);

        // Ensure minimum fare
        if (total.compareTo(MINIMUM_FARE) < 0) {
            total = MINIMUM_FARE;
        }

        return new FareBreakdown(
            baseFare,
            surgeMultiplier,
            surgedFare,
            waitingCharges,
            platformFee,
            gst,
            total
        );
    }

    /**
     * Calculates the total fare for a ride using VehicleType.
     * 
     * @param vehicleType the type of vehicle
     * @param distanceKm the distance in kilometers
     * @param durationMinutes the duration in minutes
     * @param surgeMultiplier the surge multiplier
     * @param waitingMinutes the waiting time in minutes
     * @return the total fare breakdown
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static FareBreakdown calculateTotalFare(
            VehicleType vehicleType,
            double distanceKm,
            long durationMinutes,
            BigDecimal surgeMultiplier,
            long waitingMinutes) {

        BigDecimal baseFare = calculateBaseFare(vehicleType, distanceKm, durationMinutes);
        return calculateTotalFare(baseFare, surgeMultiplier, waitingMinutes);
    }

    /**
     * Estimates the fare for a ride (without waiting charges).
     * 
     * @param vehicleType the type of vehicle
     * @param distanceKm the distance in kilometers
     * @param durationMinutes the estimated duration in minutes
     * @return the estimated fare
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static BigDecimal estimateFare(VehicleType vehicleType, double distanceKm, long durationMinutes) {
        return vehicleType.estimateFare(distanceKm);
    }
    
    /**
     * Calculates the fare for a ride (simplified version for ride service).
     *
     * @param distanceKm the distance in kilometers
     * @param durationMinutes the duration in minutes
     * @param vehicleType the type of vehicle
     * @return the calculated fare
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static BigDecimal calculateFare(double distanceKm, int durationMinutes, VehicleType vehicleType) {
        return calculateBaseFare(vehicleType, distanceKm, durationMinutes);
    }

    /**
     * Gets the cancellation fee for a rider.
     * 
     * @return the rider cancellation fee
     */
    public static BigDecimal getRiderCancellationFee() {
        return RIDER_CANCELLATION_FEE;
    }

    /**
     * Gets the cancellation fee for a driver.
     * 
     * @return the driver cancellation fee
     */
    public static BigDecimal getDriverCancellationFee() {
        return DRIVER_CANCELLATION_FEE;
    }

    /**
     * Gets the minimum fare.
     * 
     * @return the minimum fare
     */
    public static BigDecimal getMinimumFare() {
        return MINIMUM_FARE;
    }

    /**
     * Calculates the driver's earnings after platform commission.
     * Platform takes 20% commission.
     * 
     * @param totalFare the total fare paid by rider
     * @return the driver's earnings
     * @throws IllegalArgumentException if totalFare is null or negative
     */
    public static BigDecimal calculateDriverEarnings(BigDecimal totalFare) {
        validatePositive(totalFare, "Total fare");
        BigDecimal commission = totalFare.multiply(new BigDecimal("0.20"));
        BigDecimal earnings = totalFare.subtract(commission);
        return round(earnings);
    }

    /**
     * Calculates the platform commission (20% of total fare).
     * 
     * @param totalFare the total fare
     * @return the platform commission
     * @throws IllegalArgumentException if totalFare is null or negative
     */
    public static BigDecimal calculatePlatformCommission(BigDecimal totalFare) {
        validatePositive(totalFare, "Total fare");
        BigDecimal commission = totalFare.multiply(new BigDecimal("0.20"));
        return round(commission);
    }

    /**
     * Applies a discount to a fare.
     * 
     * @param fare the original fare
     * @param discountPercentage the discount percentage (e.g., 10 for 10%)
     * @return the discounted fare
     * @throws IllegalArgumentException if fare is null/negative or discount is invalid
     */
    public static BigDecimal applyDiscount(BigDecimal fare, int discountPercentage) {
        validatePositive(fare, "Fare");
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }

        BigDecimal discountMultiplier = BigDecimal.valueOf(discountPercentage).divide(
            BigDecimal.valueOf(100), DECIMAL_SCALE, ROUNDING_MODE
        );
        BigDecimal discountAmount = fare.multiply(discountMultiplier);
        BigDecimal discountedFare = fare.subtract(discountAmount);

        return round(discountedFare);
    }

    /**
     * Applies a fixed discount amount to a fare.
     * 
     * @param fare the original fare
     * @param discountAmount the discount amount
     * @return the discounted fare (minimum 0)
     * @throws IllegalArgumentException if fare or discountAmount is null or negative
     */
    public static BigDecimal applyFixedDiscount(BigDecimal fare, BigDecimal discountAmount) {
        validatePositive(fare, "Fare");
        validatePositive(discountAmount, "Discount amount");

        BigDecimal discountedFare = fare.subtract(discountAmount);

        // Ensure fare doesn't go below zero
        if (discountedFare.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return round(discountedFare);
    }

    /**
     * Formats a monetary amount to a string with currency symbol.
     * 
     * @param amount the amount to format
     * @return formatted string (e.g., "₹150.50")
     */
    public static String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "₹0.00";
        }
        return "₹" + round(amount).toPlainString();
    }

    /**
     * Rounds a BigDecimal to 2 decimal places using HALF_UP rounding.
     * 
     * @param value the value to round
     * @return the rounded value
     */
    private static BigDecimal round(BigDecimal value) {
        return value.setScale(DECIMAL_SCALE, ROUNDING_MODE);
    }

    /**
     * Validates that a BigDecimal value is not null and not negative.
     * 
     * @param value the value to validate
     * @param fieldName the name of the field (for error message)
     * @throws IllegalArgumentException if value is null or negative
     */
    private static void validatePositive(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    /**
     * Inner class representing a detailed fare breakdown.
     */
    public static class FareBreakdown {
        private final BigDecimal baseFare;
        private final BigDecimal surgeMultiplier;
        private final BigDecimal surgedFare;
        private final BigDecimal waitingCharges;
        private final BigDecimal platformFee;
        private final BigDecimal gst;
        private final BigDecimal total;

        /**
         * Constructs a FareBreakdown.
         * 
         * @param baseFare the base fare
         * @param surgeMultiplier the surge multiplier
         * @param surgedFare the fare after surge
         * @param waitingCharges the waiting charges
         * @param platformFee the platform fee
         * @param gst the GST amount
         * @param total the total fare
         */
        public FareBreakdown(
                BigDecimal baseFare,
                BigDecimal surgeMultiplier,
                BigDecimal surgedFare,
                BigDecimal waitingCharges,
                BigDecimal platformFee,
                BigDecimal gst,
                BigDecimal total) {
            this.baseFare = baseFare;
            this.surgeMultiplier = surgeMultiplier;
            this.surgedFare = surgedFare;
            this.waitingCharges = waitingCharges;
            this.platformFee = platformFee;
            this.gst = gst;
            this.total = total;
        }

        public BigDecimal getBaseFare() {
            return baseFare;
        }

        public BigDecimal getSurgeMultiplier() {
            return surgeMultiplier;
        }

        public BigDecimal getSurgedFare() {
            return surgedFare;
        }

        public BigDecimal getWaitingCharges() {
            return waitingCharges;
        }

        public BigDecimal getPlatformFee() {
            return platformFee;
        }

        public BigDecimal getGst() {
            return gst;
        }

        public BigDecimal getTotal() {
            return total;
        }

        @Override
        public String toString() {
            return "FareBreakdown{" +
                    "baseFare=" + formatAmount(baseFare) +
                    ", surgeMultiplier=" + surgeMultiplier +
                    ", surgedFare=" + formatAmount(surgedFare) +
                    ", waitingCharges=" + formatAmount(waitingCharges) +
                    ", platformFee=" + formatAmount(platformFee) +
                    ", gst=" + formatAmount(gst) +
                    ", total=" + formatAmount(total) +
                    '}';
        }
    }
}
