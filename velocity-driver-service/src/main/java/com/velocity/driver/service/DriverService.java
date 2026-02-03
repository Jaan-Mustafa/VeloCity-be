package com.velocity.driver.service;

import com.velocity.core.exception.BusinessException;
import com.velocity.core.exception.ResourceNotFoundException;
import com.velocity.driver.model.dto.DriverDto;
import com.velocity.driver.model.dto.NearbyDriverRequest;
import com.velocity.driver.model.dto.NearbyDriverResponse;
import com.velocity.driver.model.dto.RegisterDriverRequest;
import com.velocity.driver.model.dto.UpdateAvailabilityRequest;
import com.velocity.driver.model.dto.UpdateLocationRequest;
import com.velocity.driver.model.entity.Driver;
import com.velocity.driver.model.entity.DriverLocation;
import com.velocity.driver.mapper.DriverMapper;
import com.velocity.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {
    
    private final DriverRepository driverRepository;
    private final LocationService locationService;
    private final DriverMapper driverMapper;
    
    /**
     * Register a new driver
     */
    @Transactional
    public DriverDto registerDriver(RegisterDriverRequest request) {
        log.info("Registering new driver for userId: {}", request.getUserId());
        
        // Check if user is already registered as driver
        if (driverRepository.existsByUserId(request.getUserId())) {
            throw new BusinessException("User is already registered as a driver");
        }
        
        // Check if license number already exists
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new BusinessException("License number already registered");
        }
        
        // Check if vehicle number already exists
        if (driverRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new BusinessException("Vehicle number already registered");
        }
        
        // Create driver entity
        Driver driver = Driver.builder()
                .userId(request.getUserId())
                .licenseNumber(request.getLicenseNumber())
                .vehicleType(request.getVehicleType())
                .vehicleModel(request.getVehicleModel())
                .vehicleNumber(request.getVehicleNumber())
                .isAvailable(false) // Initially not available
                .rating(BigDecimal.valueOf(5.00)) // Default rating
                .totalRides(0)
                .createdAt(LocalDateTime.now())
                .build();
        
        driver = driverRepository.save(driver);
        log.info("Driver registered successfully with ID: {}", driver.getId());
        
        return driverMapper.toDto(driver);
    }
    
    /**
     * Get driver profile by driver ID
     */
    public DriverDto getDriverProfile(Long driverId) {
        log.info("Fetching driver profile for ID: {}", driverId);
        
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        DriverLocation location = locationService.getDriverLocation(driverId);
        return driverMapper.toDtoWithLocation(driver, location);
    }
    
    /**
     * Get driver profile by user ID
     */
    public DriverDto getDriverProfileByUserId(Long userId) {
        log.info("Fetching driver profile for userId: {}", userId);
        
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found for this user"));
        
        DriverLocation location = locationService.getDriverLocation(driver.getId());
        return driverMapper.toDtoWithLocation(driver, location);
    }
    
    /**
     * Update driver availability status
     */
    @Transactional
    public DriverDto updateAvailability(Long driverId, UpdateAvailabilityRequest request) {
        log.info("Updating availability for driver: {} to {}", driverId, request.getIsAvailable());
        
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        driver.setIsAvailable(request.getIsAvailable());
        driver = driverRepository.save(driver);
        
        // If going offline, remove from Redis
        if (!request.getIsAvailable()) {
            locationService.removeDriverLocation(driverId);
            log.info("Driver {} went offline, removed from Redis", driverId);
        } else {
            log.info("Driver {} is now available", driverId);
        }
        
        DriverLocation location = locationService.getDriverLocation(driverId);
        return driverMapper.toDtoWithLocation(driver, location);
    }
    
    /**
     * Update driver location
     */
    @Transactional
    public void updateLocation(Long driverId, UpdateLocationRequest request) {
        log.info("Updating location for driver: {}", driverId);
        
        // Verify driver exists
        if (!driverRepository.existsById(driverId)) {
            throw new ResourceNotFoundException("Driver not found");
        }
        
        locationService.updateDriverLocation(driverId, request.getLatitude(), request.getLongitude());
    }
    
    /**
     * Find nearby available drivers
     */
    public List<NearbyDriverResponse> findNearbyDrivers(NearbyDriverRequest request) {
        log.info("Finding nearby drivers for location: ({}, {})", 
                request.getLatitude(), request.getLongitude());
        
        return locationService.findNearbyDrivers(
                request.getLatitude(),
                request.getLongitude(),
                request.getRadiusKm(),
                request.getVehicleType(),
                request.getLimit()
        );
    }
    
    /**
     * Increment total rides for driver (called after ride completion)
     */
    @Transactional
    public void incrementTotalRides(Long driverId) {
        log.info("Incrementing total rides for driver: {}", driverId);
        
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        driver.setTotalRides(driver.getTotalRides() + 1);
        driverRepository.save(driver);
    }
    
    /**
     * Update driver rating (called after ride rating)
     */
    @Transactional
    public void updateRating(Long driverId, BigDecimal newRating) {
        log.info("Updating rating for driver: {} to {}", driverId, newRating);
        
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        // Calculate average rating (simplified - in production, store all ratings)
        BigDecimal currentRating = driver.getRating();
        int totalRides = driver.getTotalRides();
        
        if (totalRides > 0) {
            BigDecimal totalRatingPoints = currentRating.multiply(BigDecimal.valueOf(totalRides));
            totalRatingPoints = totalRatingPoints.add(newRating);
            BigDecimal averageRating = totalRatingPoints.divide(
                    BigDecimal.valueOf(totalRides + 1), 
                    2, 
                    BigDecimal.ROUND_HALF_UP
            );
            driver.setRating(averageRating);
        } else {
            driver.setRating(newRating);
        }
        
        driverRepository.save(driver);
    }
}
