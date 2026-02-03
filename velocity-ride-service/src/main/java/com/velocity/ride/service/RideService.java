package com.velocity.ride.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.velocity.core.enums.RideStatus;
import com.velocity.core.enums.VehicleType;
import com.velocity.core.exception.BusinessException;
import com.velocity.core.exception.ResourceNotFoundException;
import com.velocity.core.util.DistanceCalculator;
import com.velocity.core.util.PriceCalculator;
import com.velocity.ride.mapper.RideMapper;
import com.velocity.ride.model.dto.DriverBasicInfo;
import com.velocity.ride.model.dto.FareEstimateDto;
import com.velocity.ride.model.dto.LocationInfo;
import com.velocity.ride.model.dto.NearbyDriverDto;
import com.velocity.ride.model.dto.RideRequestDto;
import com.velocity.ride.model.dto.RideResponseDto;
import com.velocity.ride.model.dto.RideStatusDto;
import com.velocity.ride.model.entity.Ride;
import com.velocity.ride.repository.RideRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for ride booking and management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RideService {
    
    private final RideRepository rideRepository;
    private final RideMapper rideMapper;
    private final RestTemplate restTemplate;
    private final DriverMatchingService driverMatchingService;
    private final RideNotificationService notificationService;
    
    private static final String DRIVER_SERVICE_URL = "http://localhost:8082/api/drivers";
    
    /**
     * Request a new ride
     */
    @Transactional
    public RideResponseDto requestRide(Long userId, RideRequestDto request) {
        log.info("User {} requesting ride from ({}, {}) to ({}, {})",
                userId,
                request.getPickupLatitude(), request.getPickupLongitude(),
                request.getDropoffLatitude(), request.getDropoffLongitude());
        
        // Check if user already has an active ride
        if (rideRepository.existsByUserIdAndStatusIn(userId, 
                List.of(RideStatus.REQUESTED, RideStatus.ACCEPTED, RideStatus.ARRIVED, RideStatus.IN_PROGRESS))) {
            throw new BusinessException("You already have an active ride");
        }
        
        // Calculate distance
        double distanceKm = DistanceCalculator.calculateDistance(
                request.getPickupLatitude().doubleValue(),
                request.getPickupLongitude().doubleValue(),
                request.getDropoffLatitude().doubleValue(),
                request.getDropoffLongitude().doubleValue()
        );
        
        // Calculate fare
        BigDecimal fare = PriceCalculator.calculateFare(
                distanceKm,
                30, // Estimated duration in minutes
                request.getVehicleType()
        );
        
        // Create ride entity
        Ride ride = rideMapper.toEntity(request);
        ride.setUserId(userId);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setDistanceKm(BigDecimal.valueOf(distanceKm));
        ride.setFare(fare);
        ride.setRequestedAt(LocalDateTime.now());
        
        // Save ride
        ride = rideRepository.save(ride);
        log.info("Ride {} created with status REQUESTED", ride.getId());
        
        // Find nearby drivers asynchronously
        driverMatchingService.findAndNotifyDrivers(ride);
        
        // Convert to DTO and return
        return rideMapper.toDto(ride);
    }
    
    /**
     * Get fare estimate before booking
     */
    public FareEstimateDto estimateFare(
            BigDecimal pickupLat, BigDecimal pickupLon,
            BigDecimal dropoffLat, BigDecimal dropoffLon,
            VehicleType vehicleType) {
        
        log.info("Estimating fare for vehicle type {} from ({}, {}) to ({}, {})",
                vehicleType, pickupLat, pickupLon, dropoffLat, dropoffLon);
        
        // Calculate distance
        double distanceKm = DistanceCalculator.calculateDistance(
                pickupLat.doubleValue(), pickupLon.doubleValue(),
                dropoffLat.doubleValue(), dropoffLon.doubleValue()
        );
        
        // Estimate duration (assuming average speed of 30 km/h in city)
        int estimatedDuration = (int) Math.ceil(distanceKm / 30.0 * 60);
        
        // Calculate fare components
        BigDecimal baseFare = vehicleType.getBaseFare();
        BigDecimal distanceFare = BigDecimal.valueOf(distanceKm)
                .multiply(vehicleType.getPerKmRate());
        BigDecimal timeFare = BigDecimal.valueOf(estimatedDuration)
                .multiply(vehicleType.getPerMinuteRate());
        
        // Check for surge pricing (simplified - can be enhanced)
        BigDecimal surgeMultiplier = calculateSurgeMultiplier();
        BigDecimal surgeFare = BigDecimal.ZERO;
        String surgeReason = null;
        
        if (surgeMultiplier.compareTo(BigDecimal.ONE) > 0) {
            BigDecimal baseTotal = baseFare.add(distanceFare).add(timeFare);
            surgeFare = baseTotal.multiply(surgeMultiplier.subtract(BigDecimal.ONE));
            surgeReason = "High demand in your area";
        }
        
        BigDecimal totalFare = baseFare.add(distanceFare).add(timeFare).add(surgeFare);
        
        // Estimate driver arrival time (simplified)
        int estimatedArrival = estimateDriverArrivalTime(pickupLat, pickupLon, vehicleType);
        
        return FareEstimateDto.builder()
                .vehicleType(vehicleType)
                .distanceKm(BigDecimal.valueOf(distanceKm))
                .estimatedDurationMinutes(estimatedDuration)
                .baseFare(baseFare)
                .distanceFare(distanceFare)
                .timeFare(timeFare)
                .surgeFare(surgeFare)
                .totalFare(totalFare)
                .surgeMultiplier(surgeMultiplier)
                .surgeReason(surgeReason)
                .estimatedArrivalMinutes(estimatedArrival)
                .build();
    }
    
    /**
     * Get ride by ID
     */
    public RideResponseDto getRideById(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
        
        RideResponseDto dto = rideMapper.toDto(ride);
        
        // Fetch driver info if assigned
        if (ride.getDriverId() != null) {
            dto.setDriver(fetchDriverInfo(ride.getDriverId()));
        }
        
        return dto;
    }
    
    /**
     * Get user's ride history
     */
    public Page<RideResponseDto> getUserRideHistory(Long userId, Pageable pageable) {
        Page<Ride> rides = rideRepository.findByUserIdOrderByRequestedAtDesc(userId, pageable);
        return rides.map(rideMapper::toDto);
    }
    
    /**
     * Get user's active ride
     */
    public RideResponseDto getUserActiveRide(Long userId) {
        Ride ride = rideRepository.findActiveRideByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No active ride found"));
        
        RideResponseDto dto = rideMapper.toDto(ride);
        
        if (ride.getDriverId() != null) {
            dto.setDriver(fetchDriverInfo(ride.getDriverId()));
        }
        
        return dto;
    }
    
    /**
     * Cancel ride
     */
    @Transactional
    public RideResponseDto cancelRide(Long rideId, Long userId, String reason) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
        
        // Verify ownership
        if (!ride.getUserId().equals(userId)) {
            throw new BusinessException("You can only cancel your own rides");
        }
        
        // Check if ride can be cancelled
        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new BusinessException("Cannot cancel a " + ride.getStatus() + " ride");
        }
        
        // Calculate cancellation fee if driver has already accepted
        BigDecimal cancellationFee = BigDecimal.ZERO;
        if (ride.getStatus() == RideStatus.ACCEPTED || ride.getStatus() == RideStatus.ARRIVED) {
            cancellationFee = ride.getFare().multiply(BigDecimal.valueOf(0.1)); // 10% cancellation fee
        }
        
        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledBy("RIDER");
        ride.setCancellationReason(reason);
        ride.setCancellationFee(cancellationFee);
        ride.setCancelledAt(LocalDateTime.now());
        
        ride = rideRepository.save(ride);
        log.info("Ride {} cancelled by user {}", rideId, userId);
        
        // Notify driver if assigned
        if (ride.getDriverId() != null) {
            // TODO: Send notification to driver
        }
        
        return rideMapper.toDto(ride);
    }
    
    /**
     * Accept a ride (Driver)
     */
    @Transactional
    public RideResponseDto acceptRide(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
        
        // Verify ride is in REQUESTED status
        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new BusinessException("Ride is not available for acceptance");
        }
        
        // Check if driver is already assigned
        if (ride.getDriverId() != null) {
            throw new BusinessException("Ride has already been accepted by another driver");
        }
        
        // Assign driver and update status
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(LocalDateTime.now());
        
        ride = rideRepository.save(ride);
        log.info("Ride {} accepted by driver {}", rideId, driverId);
        
        // Send notification to user
        notificationService.notifyRideStatusChange(ride, RideStatus.ACCEPTED);
        
        return rideMapper.toDto(ride);
    }
    
    /**
     * Mark driver as arrived at pickup location
     */
    @Transactional
    public RideResponseDto markDriverArrived(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
        
        // Verify driver ownership
        if (!ride.getDriverId().equals(driverId)) {
            throw new BusinessException("You are not assigned to this ride");
        }
        
        // Verify ride is in ACCEPTED status
        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new BusinessException("Cannot mark arrival for ride in " + ride.getStatus() + " status");
        }
        
        // Update status to ARRIVED
        ride.setStatus(RideStatus.ARRIVED);
        ride.setArrivedAt(LocalDateTime.now());
        
        ride = rideRepository.save(ride);
        log.info("Driver {} marked arrival for ride {}", driverId, rideId);
        
        // Send notification to user
        notificationService.notifyRideStatusChange(ride, RideStatus.ARRIVED);
        
        return rideMapper.toDto(ride);
    }
    
    /**
     * Start a ride
     */
    @Transactional
    public RideResponseDto startRide(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
        
        // Verify driver ownership
        if (!ride.getDriverId().equals(driverId)) {
            throw new BusinessException("You are not assigned to this ride");
        }
        
        // Verify ride is in ARRIVED status
        if (ride.getStatus() != RideStatus.ARRIVED) {
            throw new BusinessException("Cannot start ride in " + ride.getStatus() + " status");
        }
        
        // Update status to IN_PROGRESS
        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartedAt(LocalDateTime.now());
        
        ride = rideRepository.save(ride);
        log.info("Ride {} started by driver {}", rideId, driverId);
        
        // Send notification to user
        notificationService.notifyRideStatusChange(ride, RideStatus.IN_PROGRESS);
        
        return rideMapper.toDto(ride);
    }
    
    /**
     * Complete a ride
     */
    @Transactional
    public RideResponseDto completeRide(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
        
        // Verify driver ownership
        if (!ride.getDriverId().equals(driverId)) {
            throw new BusinessException("You are not assigned to this ride");
        }
        
        // Verify ride is in IN_PROGRESS status
        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new BusinessException("Cannot complete ride in " + ride.getStatus() + " status");
        }
        
        // Update status to COMPLETED
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        
        ride = rideRepository.save(ride);
        log.info("Ride {} completed by driver {}", rideId, driverId);
        
        // Send notification to user
        notificationService.notifyRideStatusChange(ride, RideStatus.COMPLETED);
        
        // TODO: Process payment via payment service
        
        return rideMapper.toDto(ride);
    }
    
    /**
     * Get ride status for polling
     */
    public RideStatusDto getRideStatus(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
        
        // Build status message
        String statusMessage = buildStatusMessage(ride.getStatus());
        
        // Build driver info if assigned
        DriverBasicInfo driverInfo = null;
        if (ride.getDriverId() != null) {
            driverInfo = fetchDriverBasicInfo(ride.getDriverId());
        }
        
        // Build location info
        LocationInfo pickup = LocationInfo.builder()
                .latitude(ride.getPickupLatitude().doubleValue())
                .longitude(ride.getPickupLongitude().doubleValue())
                .address(ride.getPickupAddress())
                .build();
        
        LocationInfo dropoff = LocationInfo.builder()
                .latitude(ride.getDropoffLatitude().doubleValue())
                .longitude(ride.getDropoffLongitude().doubleValue())
                .address(ride.getDropoffAddress())
                .build();
        
        // Estimate arrival time if driver is on the way
        Integer estimatedArrival = null;
        if (ride.getStatus() == RideStatus.ACCEPTED) {
            estimatedArrival = 5; // Simplified - can be enhanced with real-time tracking
        }
        
        return RideStatusDto.builder()
                .rideId(ride.getId())
                .status(ride.getStatus())
                .statusMessage(statusMessage)
                .lastUpdated(getLastUpdatedTime(ride))
                .driver(driverInfo)
                .estimatedArrivalMinutes(estimatedArrival)
                .pickup(pickup)
                .dropoff(dropoff)
                .build();
    }
    
    // ==================== Private Helper Methods ====================
    
    /**
     * Calculate surge multiplier based on current demand
     * Simplified implementation - can be enhanced with real-time data
     */
    private BigDecimal calculateSurgeMultiplier() {
        // TODO: Implement real surge pricing logic based on:
        // - Current time (peak hours)
        // - Number of active rides vs available drivers
        // - Historical demand patterns
        // - Weather conditions
        // - Special events
        
        // For now, return 1.0 (no surge)
        return BigDecimal.ONE;
    }
    
    /**
     * Estimate driver arrival time
     */
    private int estimateDriverArrivalTime(BigDecimal lat, BigDecimal lon, VehicleType vehicleType) {
        try {
            // Call driver service to get nearby drivers
            List<NearbyDriverDto> nearbyDrivers = driverMatchingService
                    .findNearbyDrivers(lat, lon, vehicleType, 5.0);
            
            if (nearbyDrivers.isEmpty()) {
                return 15; // Default 15 minutes if no drivers nearby
            }
            
            // Return the closest driver's ETA
            return nearbyDrivers.get(0).getEstimatedArrivalMinutes();
            
        } catch (Exception e) {
            log.warn("Failed to estimate driver arrival time", e);
            return 10; // Default fallback
        }
    }
    
    /**
     * Fetch driver information from driver service
     */
    private RideResponseDto.DriverInfoDto fetchDriverInfo(Long driverId) {
        try {
            String url = DRIVER_SERVICE_URL + "/" + driverId;
            // TODO: Implement actual REST call to driver service
            // For now, return null - will be implemented when driver service is ready
            return null;
        } catch (Exception e) {
            log.warn("Failed to fetch driver info for driver {}", driverId, e);
            return null;
        }
    }
    
    /**
     * Fetch basic driver information for status polling
     */
    private DriverBasicInfo fetchDriverBasicInfo(Long driverId) {
        try {
            // TODO: Implement actual REST call to driver service
            // For now, return mock data
            return DriverBasicInfo.builder()
                    .driverId(driverId)
                    .name("Driver " + driverId)
                    .phone("9876543210")
                    .vehicleType("SEDAN")
                    .vehicleModel("Honda City")
                    .vehicleNumber("KA01AB1234")
                    .rating(4.5)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to fetch driver basic info for driver {}", driverId, e);
            return null;
        }
    }
    
    /**
     * Build user-friendly status message
     */
    private String buildStatusMessage(RideStatus status) {
        return switch(status) {
            case REQUESTED -> "Finding nearby drivers...";
            case ACCEPTED -> "Driver is on the way to pickup location";
            case ARRIVED -> "Driver has arrived at pickup location";
            case IN_PROGRESS -> "Ride in progress";
            case COMPLETED -> "Ride completed successfully";
            case CANCELLED -> "Ride has been cancelled";
            default -> "Ride status: " + status;
        };
    }
    
    /**
     * Get the last updated timestamp for a ride
     */
    private LocalDateTime getLastUpdatedTime(Ride ride) {
        if (ride.getCompletedAt() != null) return ride.getCompletedAt();
        if (ride.getCancelledAt() != null) return ride.getCancelledAt();
        if (ride.getStartedAt() != null) return ride.getStartedAt();
        if (ride.getArrivedAt() != null) return ride.getArrivedAt();
        if (ride.getAcceptedAt() != null) return ride.getAcceptedAt();
        return ride.getRequestedAt();
    }
}
