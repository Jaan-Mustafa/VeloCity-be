package com.velocity.driver.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velocity.core.enums.VehicleType;
import com.velocity.driver.mapper.DriverMapper;
import com.velocity.driver.model.dto.NearbyDriverResponse;
import com.velocity.driver.model.entity.Driver;
import com.velocity.driver.model.entity.DriverLocation;
import com.velocity.driver.repository.DriverLocationRepository;
import com.velocity.driver.repository.DriverRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {
    
    private static final String DRIVER_LOCATION_KEY = "driver:locations";
    private static final String AVAILABLE_DRIVERS_KEY = "driver:available";
    private static final int LOCATION_TTL_SECONDS = 30;
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final DriverRepository driverRepository;
    private final DriverLocationRepository driverLocationRepository;
    private final DriverMapper driverMapper;
    
    /**
     * Update driver location in both Redis (for fast queries) and PostgreSQL (for persistence)
     */
    @Transactional
    public void updateDriverLocation(Long driverId, Double latitude, Double longitude) {
        log.info("Updating location for driver: {} to ({}, {})", driverId, latitude, longitude);
        
        // Verify driver exists and is available
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        if (!driver.getIsAvailable()) {
            log.warn("Driver {} is not available, location not updated in Redis", driverId);
        }
        
        // Update in PostgreSQL
        DriverLocation location = driverLocationRepository.findById(driverId)
                .orElse(new DriverLocation());
        
        location.setDriverId(driverId);
        location.setLatitude(BigDecimal.valueOf(latitude));
        location.setLongitude(BigDecimal.valueOf(longitude));
        location.setUpdatedAt(LocalDateTime.now());
        driverLocationRepository.save(location);
        
        // Update in Redis Geo if driver is available
        if (driver.getIsAvailable()) {
            redisTemplate.opsForGeo().add(
                    DRIVER_LOCATION_KEY,
                    new Point(longitude, latitude),
                    driverId.toString()
            );
            
            // Set expiry on the location (driver must update regularly)
            redisTemplate.expire(DRIVER_LOCATION_KEY, LOCATION_TTL_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
            
            log.info("Driver {} location updated in Redis Geo", driverId);
        }
    }
    
    /**
     * Find nearby available drivers within specified radius
     */
    public List<NearbyDriverResponse> findNearbyDrivers(
            Double latitude, 
            Double longitude, 
            Double radiusKm,
            VehicleType vehicleType,
            Integer limit) {
        
        log.info("Finding drivers near ({}, {}) within {}km, vehicleType: {}", 
                latitude, longitude, radiusKm, vehicleType);
        
        // Query Redis Geo for nearby drivers
        Point center = new Point(longitude, latitude);
        Distance radius = new Distance(radiusKm, Metrics.KILOMETERS);
        Circle area = new Circle(center, radius);
        
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = 
                redisTemplate.opsForGeo().radius(DRIVER_LOCATION_KEY, area);
        
        if (results == null || results.getContent().isEmpty()) {
            log.info("No nearby drivers found in Redis");
            return new ArrayList<>();
        }
        
        // Extract driver IDs and distances
        List<NearbyDriverResponse> nearbyDrivers = results.getContent().stream()
                .map(result -> {
                    Long driverId = Long.parseLong(result.getContent().getName().toString());
                    Double distanceKm = result.getDistance().getValue();
                    Point point = result.getContent().getPoint();
                    
                    // Fetch driver details from database
                    return driverRepository.findById(driverId)
                            .filter(Driver::getIsAvailable)
                            .filter(driver -> vehicleType == null || driver.getVehicleType().equals(vehicleType))
                            .map(driver -> driverMapper.toNearbyDriverResponse(
                                    driver, 
                                    point.getY(), // latitude
                                    point.getX(), // longitude
                                    distanceKm
                            ))
                            .orElse(null);
                })
                .filter(response -> response != null)
                .limit(limit)
                .collect(Collectors.toList());
        
        log.info("Found {} nearby drivers", nearbyDrivers.size());
        return nearbyDrivers;
    }
    
    /**
     * Remove driver location from Redis when they go offline
     */
    public void removeDriverLocation(Long driverId) {
        log.info("Removing driver {} from Redis Geo", driverId);
        redisTemplate.opsForGeo().remove(DRIVER_LOCATION_KEY, driverId.toString());
    }
    
    /**
     * Get driver's current location
     */
    public DriverLocation getDriverLocation(Long driverId) {
        return driverLocationRepository.findById(driverId)
                .orElse(null);
    }
}
