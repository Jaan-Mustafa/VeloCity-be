package com.velocity.driver.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.velocity.core.enums.VehicleType;
import com.velocity.driver.model.entity.Driver;

/**
 * Repository interface for Driver entity.
 * Provides CRUD operations and custom queries for driver management.
 * 
 * Following rules.md:
 * - Extends JpaRepository for standard operations
 * - Custom query methods for driver-specific operations
 * - JPQL queries for complex operations
 */
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    /**
     * Find driver by user ID
     * @param userId User ID
     * @return Optional containing driver if found
     */
    Optional<Driver> findByUserId(Long userId);
    
    /**
     * Check if user is already registered as driver
     * @param userId User ID
     * @return true if exists
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Find driver by license number
     * @param licenseNumber License number
     * @return Optional containing driver if found
     */
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    
    /**
     * Find driver by vehicle number
     * @param vehicleNumber Vehicle number
     * @return Optional containing driver if found
     */
    Optional<Driver> findByVehicleNumber(String vehicleNumber);
    
    /**
     * Check if license number exists
     * @param licenseNumber License number
     * @return true if exists
     */
    boolean existsByLicenseNumber(String licenseNumber);
    
    /**
     * Check if vehicle number exists
     * @param vehicleNumber Vehicle number
     * @return true if exists
     */
    boolean existsByVehicleNumber(String vehicleNumber);
    
    /**
     * Find all available drivers
     * @return List of available drivers
     */
    List<Driver> findByIsAvailableTrue();
    
    /**
     * Find all available drivers by vehicle type
     * @param vehicleType Vehicle type
     * @return List of available drivers with specified vehicle type
     */
    List<Driver> findByIsAvailableTrueAndVehicleType(VehicleType vehicleType);
    
    /**
     * Find all verified drivers
     * @return List of verified drivers
     */
    List<Driver> findByIsVerifiedTrue();
    
    /**
     * Find drivers with rating above threshold
     * @param minRating Minimum rating
     * @return List of drivers with rating >= minRating
     */
    @Query("SELECT d FROM Driver d WHERE d.rating >= :minRating ORDER BY d.rating DESC")
    List<Driver> findByRatingGreaterThanEqual(@Param("minRating") BigDecimal minRating);
    
    /**
     * Find top rated drivers
     * @param limit Number of drivers to return
     * @return List of top rated drivers
     */
    @Query("SELECT d FROM Driver d WHERE d.isVerified = true ORDER BY d.rating DESC, d.totalRides DESC")
    List<Driver> findTopRatedDrivers(@Param("limit") int limit);
}
