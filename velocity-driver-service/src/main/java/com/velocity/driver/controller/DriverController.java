package com.velocity.driver.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velocity.core.dto.ApiResponse;
import com.velocity.driver.model.dto.DriverDto;
import com.velocity.driver.model.dto.NearbyDriverRequest;
import com.velocity.driver.model.dto.NearbyDriverResponse;
import com.velocity.driver.model.dto.RegisterDriverRequest;
import com.velocity.driver.model.dto.UpdateAvailabilityRequest;
import com.velocity.driver.model.dto.UpdateLocationRequest;
import com.velocity.driver.service.DriverService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Driver management operations.
 * Handles driver registration, availability, location updates, and nearby driver search.
 */
@Slf4j
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {
    
    private final DriverService driverService;
    
    /**
     * Register a new driver
     * POST /api/drivers/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DriverDto>> registerDriver(
            @Valid @RequestBody RegisterDriverRequest request) {
        log.info("Received driver registration request for userId: {}", request.getUserId());
        
        DriverDto driver = driverService.registerDriver(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(driver, "Driver registered successfully"));
    }
    
    /**
     * Get driver profile by driver ID
     * GET /api/drivers/{driverId}
     */
    @GetMapping("/{driverId}")
    public ResponseEntity<ApiResponse<DriverDto>> getDriverProfile(
            @PathVariable Long driverId) {
        log.info("Fetching driver profile for ID: {}", driverId);
        
        DriverDto driver = driverService.getDriverProfile(driverId);
        
        return ResponseEntity.ok(ApiResponse.success(driver));
    }
    
    /**
     * Get driver profile by user ID
     * GET /api/drivers/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<DriverDto>> getDriverProfileByUserId(
            @PathVariable Long userId) {
        log.info("Fetching driver profile for userId: {}", userId);
        
        DriverDto driver = driverService.getDriverProfileByUserId(userId);
        
        return ResponseEntity.ok(ApiResponse.success(driver));
    }
    
    /**
     * Update driver availability status
     * PUT /api/drivers/{driverId}/availability
     */
    @PutMapping("/{driverId}/availability")
    public ResponseEntity<ApiResponse<DriverDto>> updateAvailability(
            @PathVariable Long driverId,
            @Valid @RequestBody UpdateAvailabilityRequest request) {
        log.info("Updating availability for driver: {} to {}", driverId, request.getIsAvailable());
        
        DriverDto driver = driverService.updateAvailability(driverId, request);
        
        return ResponseEntity.ok(
                ApiResponse.success(driver, "Availability updated successfully"));
    }
    
    /**
     * Update driver location
     * POST /api/drivers/{driverId}/location
     */
    @PostMapping("/{driverId}/location")
    public ResponseEntity<ApiResponse<Void>> updateLocation(
            @PathVariable Long driverId,
            @Valid @RequestBody UpdateLocationRequest request) {
        log.info("Updating location for driver: {}", driverId);
        
        driverService.updateLocation(driverId, request);
        
        return ResponseEntity.ok(
                ApiResponse.success(null, "Location updated successfully"));
    }
    
    /**
     * Find nearby available drivers
     * POST /api/drivers/nearby
     */
    @PostMapping("/nearby")
    public ResponseEntity<ApiResponse<List<NearbyDriverResponse>>> findNearbyDrivers(
            @Valid @RequestBody NearbyDriverRequest request) {
        log.info("Finding nearby drivers for location: ({}, {})", 
                request.getLatitude(), request.getLongitude());
        
        List<NearbyDriverResponse> drivers = driverService.findNearbyDrivers(request);
        
        return ResponseEntity.ok(
                ApiResponse.success(drivers, 
                        String.format("Found %d nearby drivers", drivers.size())));
    }
}
