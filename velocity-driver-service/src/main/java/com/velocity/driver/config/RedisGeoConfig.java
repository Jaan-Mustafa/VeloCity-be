package com.velocity.driver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis Geospatial configuration for Driver Service.
 * Provides geospatial operations for tracking driver locations.
 * 
 * Following rules.md:
 * - Geospatial operations for nearby driver queries
 * - Efficient location storage and retrieval
 * - Distance-based searches
 * 
 * Note: This is a helper configuration. The actual geospatial operations
 * will be used in the service layer for finding nearby drivers.
 */
@Configuration
public class RedisGeoConfig {
    
    public static final String DRIVER_LOCATIONS_KEY = "driver:locations";
    public static final String AVAILABLE_DRIVERS_KEY = "driver:available";
    
    /**
     * Configure RedisTemplate for geospatial operations
     * @param connectionFactory Redis connection factory
     * @return Configured RedisTemplate for geo operations
     */
    @Bean
    public RedisTemplate<String, String> geoRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.afterPropertiesSet();
        return template;
    }
    
    /**
     * Helper method to create a Point from coordinates
     * @param longitude Longitude
     * @param latitude Latitude
     * @return Point object
     */
    public static Point createPoint(double longitude, double latitude) {
        return new Point(longitude, latitude);
    }
    
    /**
     * Helper method to create a Circle for radius search
     * @param longitude Center longitude
     * @param latitude Center latitude
     * @param radiusKm Radius in kilometers
     * @return Circle object
     */
    public static Circle createCircle(double longitude, double latitude, double radiusKm) {
        Point center = new Point(longitude, latitude);
        Distance radius = new Distance(radiusKm, Metrics.KILOMETERS);
        return new Circle(center, radius);
    }
    
    /**
     * Helper method to create Distance object
     * @param value Distance value
     * @param unit Distance unit (KILOMETERS, METERS, etc.)
     * @return Distance object
     */
    public static Distance createDistance(double value, Metrics unit) {
        return new Distance(value, unit);
    }
}
