package com.velocity.ride.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.velocity.core.dto.ApiResponse;
import com.velocity.core.enums.VehicleType;
import com.velocity.ride.model.dto.FareEstimateDto;
import com.velocity.ride.model.dto.RideRequestDto;
import com.velocity.ride.model.dto.RideResponseDto;
import com.velocity.ride.model.dto.RideStatusDto;
import com.velocity.ride.service.RideService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for ride booking and management
 */
@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@Slf4j
public class RideController {
    
    private final RideService rideService;
    
    /**
     * Request a new ride
     * POST /api/rides/request
     */
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<RideResponseDto>> requestRide(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody RideRequestDto request) {
        
        log.info("Ride request received from user {}", userId);
        RideResponseDto ride = rideService.requestRide(userId, request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(ride, "Ride requested successfully"));
    }
    
    /**
     * Get fare estimate
     * GET /api/rides/estimate
     */
    @GetMapping("/estimate")
    public ResponseEntity<ApiResponse<FareEstimateDto>> estimateFare(
            @RequestParam BigDecimal pickupLat,
            @RequestParam BigDecimal pickupLon,
            @RequestParam BigDecimal dropoffLat,
            @RequestParam BigDecimal dropoffLon,
            @RequestParam VehicleType vehicleType) {
        
        log.info("Fare estimate request for vehicle type {}", vehicleType);
        FareEstimateDto estimate = rideService.estimateFare(
                pickupLat, pickupLon, dropoffLat, dropoffLon, vehicleType);
        
        return ResponseEntity.ok(ApiResponse.success(estimate, "Fare estimated successfully"));
    }
    
    /**
     * Get ride by ID
     * GET /api/rides/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RideResponseDto>> getRideById(@PathVariable Long id) {
        log.info("Fetching ride {}", id);
        RideResponseDto ride = rideService.getRideById(id);
        return ResponseEntity.ok(ApiResponse.success(ride));
    }
    
    /**
     * Get user's ride history
     * GET /api/rides/history
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<RideResponseDto>>> getRideHistory(
            @RequestHeader("X-User-Id") Long userId,
            @PageableDefault(size = 20, sort = "requestedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Fetching ride history for user {}", userId);
        Page<RideResponseDto> rides = rideService.getUserRideHistory(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(rides));
    }
    
    /**
     * Get user's active ride
     * GET /api/rides/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<RideResponseDto>> getActiveRide(
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("Fetching active ride for user {}", userId);
        RideResponseDto ride = rideService.getUserActiveRide(userId);
        return ResponseEntity.ok(ApiResponse.success(ride));
    }
    
    /**
     * Cancel a ride
     * POST /api/rides/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<RideResponseDto>> cancelRide(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String reason) {
        
        log.info("User {} cancelling ride {}", userId, id);
        RideResponseDto ride = rideService.cancelRide(id, userId, reason);
        return ResponseEntity.ok(ApiResponse.success(ride, "Ride cancelled successfully"));
    }
    
    /**
     * Accept a ride (Driver)
     * POST /api/rides/{id}/accept
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<RideResponseDto>> acceptRide(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long driverId) {
        
        log.info("Driver {} accepting ride {}", driverId, id);
        RideResponseDto ride = rideService.acceptRide(id, driverId);
        return ResponseEntity.ok(ApiResponse.success(ride, "Ride accepted successfully"));
    }
    
    /**
     * Mark driver as arrived at pickup location
     * POST /api/rides/{id}/driver-arrived
     */
    @PostMapping("/{id}/driver-arrived")
    public ResponseEntity<ApiResponse<RideResponseDto>> markDriverArrived(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long driverId) {
        
        log.info("Driver {} marking arrival for ride {}", driverId, id);
        RideResponseDto ride = rideService.markDriverArrived(id, driverId);
        return ResponseEntity.ok(ApiResponse.success(ride, "Driver arrival marked successfully"));
    }
    
    /**
     * Start a ride
     * POST /api/rides/{id}/start
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<RideResponseDto>> startRide(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long driverId) {
        
        log.info("Driver {} starting ride {}", driverId, id);
        RideResponseDto ride = rideService.startRide(id, driverId);
        return ResponseEntity.ok(ApiResponse.success(ride, "Ride started successfully"));
    }
    
    /**
     * Complete a ride
     * POST /api/rides/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<RideResponseDto>> completeRide(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long driverId) {
        
        log.info("Driver {} completing ride {}", driverId, id);
        RideResponseDto ride = rideService.completeRide(id, driverId);
        return ResponseEntity.ok(ApiResponse.success(ride, "Ride completed successfully"));
    }
    
    /**
     * Get ride status (for polling)
     * GET /api/rides/{id}/status
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RideStatusDto>> getRideStatus(@PathVariable Long id) {
        log.info("Fetching status for ride {}", id);
        RideStatusDto status = rideService.getRideStatus(id);
        return ResponseEntity.ok(ApiResponse.success(status));
    }
}
