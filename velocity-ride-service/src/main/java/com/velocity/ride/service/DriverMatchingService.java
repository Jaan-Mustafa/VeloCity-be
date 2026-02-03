package com.velocity.ride.service;

import java.math.BigDecimal;
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
import org.springframework.web.client.RestTemplate;

import com.velocity.core.enums.VehicleType;
import com.velocity.ride.model.dto.NearbyDriverDto;
import com.velocity.ride.model.entity.Ride;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for matching rides with nearby available drivers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverMatchingService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;
    
    private static final String DRIVER_LOCATION_KEY = "driver:locations";
    private static final String DRIVER_SERVICE_URL = "http://localhost:8082/api/drivers";
    private static final double DEFAULT_SEARCH_RADIUS_KM = 5.0;
    private static final int MAX_DRIVERS_TO_NOTIFY = 3;
    
    /**
     * Find nearby available drivers for a ride
     */
    public List<NearbyDriverDto> findNearbyDrivers(
            BigDecimal latitude,
            BigDecimal longitude,
            VehicleType vehicleType,
            double radiusKm) {
        
        log.info("Searching for {} drivers within {}km of ({}, {})",
                vehicleType, radiusKm, latitude, longitude);
        
        try {
            // Create search area
            Point center = new Point(longitude.doubleValue(), latitude.doubleValue());
            Distance radius = new Distance(radiusKm, Metrics.KILOMETERS);
            Circle searchArea = new Circle(center, radius);
            
            // Query Redis for nearby drivers
            GeoResults<RedisGeoCommands.GeoLocation<Object>> results = 
                    redisTemplate.opsForGeo().radius(DRIVER_LOCATION_KEY, searchArea);
            
            if (results == null || results.getContent().isEmpty()) {
                log.warn("No drivers found within {}km", radiusKm);
                return new ArrayList<>();
            }
            
            // Convert results to DTOs
            List<NearbyDriverDto> nearbyDrivers = results.getContent().stream()
                    .map(result -> {
                        String driverId = result.getContent().getName().toString();
                        Point driverLocation = result.getContent().getPoint();
                        double distanceKm = result.getDistance().getValue();
                        
                        // Create basic DTO (will be enhanced with driver service call)
                        return NearbyDriverDto.builder()
                                .driverId(Long.parseLong(driverId))
                                .latitude(BigDecimal.valueOf(driverLocation.getY()))
                                .longitude(BigDecimal.valueOf(driverLocation.getX()))
                                .distanceKm(BigDecimal.valueOf(distanceKm))
                                .isAvailable(true)
                                .estimatedArrivalMinutes((int) Math.ceil(distanceKm / 30.0 * 60))
                                .build();
                    })
                    .filter(driver -> vehicleType == null || driver.getVehicleType() == vehicleType)
                    .sorted((d1, d2) -> d1.getDistanceKm().compareTo(d2.getDistanceKm()))
                    .collect(Collectors.toList());
            
            log.info("Found {} available drivers", nearbyDrivers.size());
            return nearbyDrivers;
            
        } catch (Exception e) {
            log.error("Error finding nearby drivers", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Find and notify drivers about a new ride request
     * This will be called asynchronously after ride creation
     */
    public void findAndNotifyDrivers(Ride ride) {
        log.info("Finding drivers for ride {}", ride.getId());
        
        try {
            // Find nearby drivers
            List<NearbyDriverDto> nearbyDrivers = findNearbyDrivers(
                    ride.getPickupLatitude(),
                    ride.getPickupLongitude(),
                    ride.getVehicleType(),
                    DEFAULT_SEARCH_RADIUS_KM
            );
            
            if (nearbyDrivers.isEmpty()) {
                log.warn("No drivers available for ride {}", ride.getId());
                // TODO: Notify user that no drivers are available
                return;
            }
            
            // Notify top N drivers
            int driversToNotify = Math.min(nearbyDrivers.size(), MAX_DRIVERS_TO_NOTIFY);
            for (int i = 0; i < driversToNotify; i++) {
                NearbyDriverDto driver = nearbyDrivers.get(i);
                notifyDriver(driver.getDriverId(), ride);
            }
            
            log.info("Notified {} drivers for ride {}", driversToNotify, ride.getId());
            
        } catch (Exception e) {
            log.error("Error notifying drivers for ride {}", ride.getId(), e);
        }
    }
    
    /**
     * Notify a specific driver about a ride request
     * TODO: Implement WebSocket notification
     */
    private void notifyDriver(Long driverId, Ride ride) {
        log.info("Notifying driver {} about ride {}", driverId, ride.getId());
        // TODO: Send WebSocket notification to driver
        // For now, just log it
    }
}
