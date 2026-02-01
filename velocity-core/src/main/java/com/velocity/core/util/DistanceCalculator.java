package com.velocity.core.util;

import com.velocity.core.dto.CoordinatesDto;
import com.velocity.core.dto.LocationDto;

/**
 * Utility class for calculating geographical distances between coordinates.
 * Uses the Haversine formula to calculate great-circle distances on Earth.
 * 
 * <p>The Haversine formula determines the shortest distance over the earth's surface,
 * giving an "as-the-crow-flies" distance between two points (ignoring any hills, valleys, etc.).</p>
 * 
 * <p>All methods are static and the class cannot be instantiated.</p>
 * 
 * @author VeloCity Team
 * @version 1.0
 * @since 2026-02-01
 */
public final class DistanceCalculator {

    /**
     * Earth's mean radius in kilometers.
     * Using the mean radius as defined by IUGG (International Union of Geodesy and Geophysics).
     */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Earth's mean radius in meters.
     */
    private static final double EARTH_RADIUS_M = EARTH_RADIUS_KM * 1000;

    /**
     * Conversion factor from degrees to radians.
     */
    private static final double DEGREES_TO_RADIANS = Math.PI / 180.0;

    /**
     * Private constructor to prevent instantiation.
     * 
     * @throws UnsupportedOperationException if instantiation is attempted
     */
    private DistanceCalculator() {
        throw new UnsupportedOperationException("DistanceCalculator is a utility class and cannot be instantiated");
    }

    /**
     * Calculates the distance between two coordinates using the Haversine formula.
     * 
     * @param lat1 latitude of the first point in degrees
     * @param lon1 longitude of the first point in degrees
     * @param lat2 latitude of the second point in degrees
     * @param lon2 longitude of the second point in degrees
     * @return distance in kilometers
     * @throws IllegalArgumentException if any coordinate is invalid
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        validateCoordinates(lat1, lon1);
        validateCoordinates(lat2, lon2);

        // Convert degrees to radians
        double lat1Rad = lat1 * DEGREES_TO_RADIANS;
        double lon1Rad = lon1 * DEGREES_TO_RADIANS;
        double lat2Rad = lat2 * DEGREES_TO_RADIANS;
        double lon2Rad = lon2 * DEGREES_TO_RADIANS;

        // Haversine formula
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates the distance between two LocationDto objects.
     * 
     * @param location1 the first location
     * @param location2 the second location
     * @return distance in kilometers
     * @throws IllegalArgumentException if any location is null or has invalid coordinates
     */
    public static double calculateDistance(LocationDto location1, LocationDto location2) {
        if (location1 == null || location2 == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }

        return calculateDistance(
            location1.getLatitude(),
            location1.getLongitude(),
            location2.getLatitude(),
            location2.getLongitude()
        );
    }

    /**
     * Calculates the distance between two CoordinatesDto objects.
     * 
     * @param coords1 the first coordinates
     * @param coords2 the second coordinates
     * @return distance in kilometers
     * @throws IllegalArgumentException if any coordinates are null or invalid
     */
    public static double calculateDistance(CoordinatesDto coords1, CoordinatesDto coords2) {
        if (coords1 == null || coords2 == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }

        return calculateDistance(
            coords1.getLatitude(),
            coords1.getLongitude(),
            coords2.getLatitude(),
            coords2.getLongitude()
        );
    }

    /**
     * Calculates the distance in meters between two coordinates.
     * 
     * @param lat1 latitude of the first point in degrees
     * @param lon1 longitude of the first point in degrees
     * @param lat2 latitude of the second point in degrees
     * @param lon2 longitude of the second point in degrees
     * @return distance in meters
     * @throws IllegalArgumentException if any coordinate is invalid
     */
    public static double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        return calculateDistance(lat1, lon1, lat2, lon2) * 1000;
    }

    /**
     * Calculates the distance in meters between two LocationDto objects.
     * 
     * @param location1 the first location
     * @param location2 the second location
     * @return distance in meters
     * @throws IllegalArgumentException if any location is null or has invalid coordinates
     */
    public static double calculateDistanceInMeters(LocationDto location1, LocationDto location2) {
        return calculateDistance(location1, location2) * 1000;
    }

    /**
     * Calculates the distance in meters between two CoordinatesDto objects.
     * 
     * @param coords1 the first coordinates
     * @param coords2 the second coordinates
     * @return distance in meters
     * @throws IllegalArgumentException if any coordinates are null or invalid
     */
    public static double calculateDistanceInMeters(CoordinatesDto coords1, CoordinatesDto coords2) {
        return calculateDistance(coords1, coords2) * 1000;
    }

    /**
     * Checks if two locations are within a specified distance (in kilometers).
     * 
     * @param location1 the first location
     * @param location2 the second location
     * @param maxDistanceKm the maximum distance in kilometers
     * @return true if the locations are within the specified distance, false otherwise
     * @throws IllegalArgumentException if any location is null or maxDistanceKm is negative
     */
    public static boolean isWithinDistance(LocationDto location1, LocationDto location2, double maxDistanceKm) {
        if (maxDistanceKm < 0) {
            throw new IllegalArgumentException("Maximum distance cannot be negative");
        }
        double distance = calculateDistance(location1, location2);
        return distance <= maxDistanceKm;
    }

    /**
     * Checks if two coordinates are within a specified distance (in kilometers).
     * 
     * @param coords1 the first coordinates
     * @param coords2 the second coordinates
     * @param maxDistanceKm the maximum distance in kilometers
     * @return true if the coordinates are within the specified distance, false otherwise
     * @throws IllegalArgumentException if any coordinates are null or maxDistanceKm is negative
     */
    public static boolean isWithinDistance(CoordinatesDto coords1, CoordinatesDto coords2, double maxDistanceKm) {
        if (maxDistanceKm < 0) {
            throw new IllegalArgumentException("Maximum distance cannot be negative");
        }
        double distance = calculateDistance(coords1, coords2);
        return distance <= maxDistanceKm;
    }

    /**
     * Checks if two locations are within a specified distance (in meters).
     * 
     * @param location1 the first location
     * @param location2 the second location
     * @param maxDistanceMeters the maximum distance in meters
     * @return true if the locations are within the specified distance, false otherwise
     * @throws IllegalArgumentException if any location is null or maxDistanceMeters is negative
     */
    public static boolean isWithinDistanceMeters(LocationDto location1, LocationDto location2, double maxDistanceMeters) {
        if (maxDistanceMeters < 0) {
            throw new IllegalArgumentException("Maximum distance cannot be negative");
        }
        double distanceMeters = calculateDistanceInMeters(location1, location2);
        return distanceMeters <= maxDistanceMeters;
    }

    /**
     * Formats a distance in kilometers to a human-readable string.
     * 
     * @param distanceKm the distance in kilometers
     * @return formatted string (e.g., "1.5 km", "500 m")
     */
    public static String formatDistance(double distanceKm) {
        if (distanceKm < 1.0) {
            // Show in meters if less than 1 km
            int meters = (int) Math.round(distanceKm * 1000);
            return meters + " m";
        } else if (distanceKm < 10.0) {
            // Show one decimal place for distances less than 10 km
            return String.format("%.1f km", distanceKm);
        } else {
            // Show whole kilometers for longer distances
            return String.format("%.0f km", distanceKm);
        }
    }

    /**
     * Calculates the estimated travel time based on distance and average speed.
     * 
     * @param distanceKm the distance in kilometers
     * @param averageSpeedKmh the average speed in kilometers per hour
     * @return estimated travel time in minutes
     * @throws IllegalArgumentException if distance or speed is negative or speed is zero
     */
    public static long estimateTravelTime(double distanceKm, double averageSpeedKmh) {
        if (distanceKm < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        if (averageSpeedKmh <= 0) {
            throw new IllegalArgumentException("Average speed must be positive");
        }

        double hours = distanceKm / averageSpeedKmh;
        return Math.round(hours * 60); // Convert to minutes
    }

    /**
     * Calculates the estimated travel time between two locations.
     * Uses a default average speed of 30 km/h (typical urban traffic).
     * 
     * @param location1 the first location
     * @param location2 the second location
     * @return estimated travel time in minutes
     * @throws IllegalArgumentException if any location is null or has invalid coordinates
     */
    public static long estimateTravelTime(LocationDto location1, LocationDto location2) {
        double distanceKm = calculateDistance(location1, location2);
        return estimateTravelTime(distanceKm, 30.0); // Default 30 km/h for urban traffic
    }

    /**
     * Calculates the estimated travel time between two coordinates.
     * Uses a default average speed of 30 km/h (typical urban traffic).
     * 
     * @param coords1 the first coordinates
     * @param coords2 the second coordinates
     * @return estimated travel time in minutes
     * @throws IllegalArgumentException if any coordinates are null or invalid
     */
    public static long estimateTravelTime(CoordinatesDto coords1, CoordinatesDto coords2) {
        double distanceKm = calculateDistance(coords1, coords2);
        return estimateTravelTime(distanceKm, 30.0); // Default 30 km/h for urban traffic
    }

    /**
     * Validates that coordinates are within valid ranges.
     * Latitude must be between -90 and 90 degrees.
     * Longitude must be between -180 and 180 degrees.
     * 
     * @param latitude the latitude to validate
     * @param longitude the longitude to validate
     * @throws IllegalArgumentException if coordinates are invalid
     */
    private static void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException(
                String.format("Invalid latitude: %.6f. Must be between -90 and 90 degrees.", latitude)
            );
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException(
                String.format("Invalid longitude: %.6f. Must be between -180 and 180 degrees.", longitude)
            );
        }
    }

    /**
     * Calculates the bearing (direction) from one point to another.
     * The bearing is the angle in degrees clockwise from north.
     * 
     * @param lat1 latitude of the first point in degrees
     * @param lon1 longitude of the first point in degrees
     * @param lat2 latitude of the second point in degrees
     * @param lon2 longitude of the second point in degrees
     * @return bearing in degrees (0-360), where 0 is north, 90 is east, 180 is south, 270 is west
     * @throws IllegalArgumentException if any coordinate is invalid
     */
    public static double calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        validateCoordinates(lat1, lon1);
        validateCoordinates(lat2, lon2);

        // Convert to radians
        double lat1Rad = lat1 * DEGREES_TO_RADIANS;
        double lat2Rad = lat2 * DEGREES_TO_RADIANS;
        double dLon = (lon2 - lon1) * DEGREES_TO_RADIANS;

        double y = Math.sin(dLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                   Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(dLon);

        double bearingRad = Math.atan2(y, x);
        double bearingDeg = bearingRad / DEGREES_TO_RADIANS;

        // Normalize to 0-360
        return (bearingDeg + 360) % 360;
    }

    /**
     * Converts a bearing to a compass direction (N, NE, E, SE, S, SW, W, NW).
     * 
     * @param bearing the bearing in degrees (0-360)
     * @return compass direction as a string
     */
    public static String bearingToCompassDirection(double bearing) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int index = (int) Math.round(((bearing % 360) / 45.0)) % 8;
        return directions[index];
    }

    /**
     * Calculates a destination point given a starting point, bearing, and distance.
     * 
     * @param lat the starting latitude in degrees
     * @param lon the starting longitude in degrees
     * @param bearing the bearing in degrees (0-360)
     * @param distanceKm the distance in kilometers
     * @return the destination coordinates
     * @throws IllegalArgumentException if coordinates or bearing are invalid
     */
    public static CoordinatesDto calculateDestination(double lat, double lon, double bearing, double distanceKm) {
        validateCoordinates(lat, lon);

        double latRad = lat * DEGREES_TO_RADIANS;
        double lonRad = lon * DEGREES_TO_RADIANS;
        double bearingRad = bearing * DEGREES_TO_RADIANS;
        double angularDistance = distanceKm / EARTH_RADIUS_KM;

        double destLatRad = Math.asin(
            Math.sin(latRad) * Math.cos(angularDistance) +
            Math.cos(latRad) * Math.sin(angularDistance) * Math.cos(bearingRad)
        );

        double destLonRad = lonRad + Math.atan2(
            Math.sin(bearingRad) * Math.sin(angularDistance) * Math.cos(latRad),
            Math.cos(angularDistance) - Math.sin(latRad) * Math.sin(destLatRad)
        );

        double destLat = destLatRad / DEGREES_TO_RADIANS;
        double destLon = destLonRad / DEGREES_TO_RADIANS;

        // Normalize longitude to -180 to 180
        destLon = ((destLon + 540) % 360) - 180;

        return CoordinatesDto.of(destLat, destLon);
    }
}
