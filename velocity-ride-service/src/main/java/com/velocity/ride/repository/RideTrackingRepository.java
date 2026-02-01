package com.velocity.ride.repository;

import com.velocity.ride.model.entity.RideTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for RideTracking entity.
 * Provides operations for tracking ride routes.
 * 
 * Following rules.md:
 * - Extends JpaRepository for standard operations
 * - Custom queries for route tracking
 */
@Repository
public interface RideTrackingRepository extends JpaRepository<RideTracking, Long> {
    
    /**
     * Find all tracking points for a ride, ordered by time
     * @param rideId Ride ID
     * @return List of tracking points
     */
    List<RideTracking> findByRideIdOrderByRecordedAtAsc(Long rideId);
    
    /**
     * Find latest tracking point for a ride
     * @param rideId Ride ID
     * @return List containing the latest tracking point
     */
    @Query("SELECT rt FROM RideTracking rt WHERE rt.rideId = :rideId ORDER BY rt.recordedAt DESC")
    List<RideTracking> findLatestByRideId(@Param("rideId") Long rideId);
    
    /**
     * Count tracking points for a ride
     * @param rideId Ride ID
     * @return Count of tracking points
     */
    long countByRideId(Long rideId);
    
    /**
     * Delete all tracking points for a ride
     * @param rideId Ride ID
     */
    void deleteByRideId(Long rideId);
}
