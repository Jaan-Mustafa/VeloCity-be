package com.velocity.driver.repository;

import com.velocity.driver.model.entity.DriverLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for DriverLocation entity.
 * Provides operations for tracking driver locations.
 * 
 * Following rules.md:
 * - Extends JpaRepository for standard operations
 * - Custom queries for location-based operations
 * - Note: For production, consider using Redis Geo for nearby queries
 */
@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation, Long> {
    
    /**
     * Find location by driver ID
     * @param driverId Driver ID
     * @return Optional containing driver location if found
     */
    Optional<DriverLocation> findByDriverId(Long driverId);
    
    /**
     * Delete location by driver ID
     * @param driverId Driver ID
     */
    void deleteByDriverId(Long driverId);
    
    /**
     * Delete stale locations (older than specified time)
     * @param cutoffTime Cutoff time
     * @return Number of deleted records
     */
    @Modifying
    @Query("DELETE FROM DriverLocation dl WHERE dl.updatedAt < :cutoffTime")
    int deleteStaleLocations(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Count active driver locations (updated within last N minutes)
     * @param cutoffTime Cutoff time
     * @return Count of active locations
     */
    @Query("SELECT COUNT(dl) FROM DriverLocation dl WHERE dl.updatedAt >= :cutoffTime")
    long countActiveLocations(@Param("cutoffTime") LocalDateTime cutoffTime);
}
