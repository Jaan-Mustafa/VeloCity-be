package com.velocity.ride.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.velocity.core.enums.RideStatus;
import com.velocity.core.enums.VehicleType;
import com.velocity.ride.model.entity.Ride;

/**
 * Repository interface for Ride entity.
 * Provides CRUD operations and custom queries for ride management.
 * 
 * Following rules.md:
 * - Extends JpaRepository for standard operations
 * - Paginated queries for ride history
 * - Complex queries using JPQL
 */
@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    
    /**
     * Find rides by user ID with pagination
     * @param userId User ID
     * @param pageable Pagination parameters
     * @return Page of rides
     */
    Page<Ride> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find rides by driver ID with pagination
     * @param driverId Driver ID
     * @param pageable Pagination parameters
     * @return Page of rides
     */
    Page<Ride> findByDriverId(Long driverId, Pageable pageable);
    
    /**
     * Find rides by status
     * @param status Ride status
     * @return List of rides with specified status
     */
    List<Ride> findByStatus(RideStatus status);
    
    /**
     * Find rides by user and status
     * @param userId User ID
     * @param status Ride status
     * @return List of rides
     */
    List<Ride> findByUserIdAndStatus(Long userId, RideStatus status);
    
    /**
     * Find rides by driver and status
     * @param driverId Driver ID
     * @param status Ride status
     * @return List of rides
     */
    List<Ride> findByDriverIdAndStatus(Long driverId, RideStatus status);
    
    /**
     * Find active ride for user (REQUESTED, ACCEPTED, ARRIVED, IN_PROGRESS)
     * @param userId User ID
     * @return Optional containing active ride if found
     */
    @Query("SELECT r FROM Ride r WHERE r.userId = :userId AND r.status IN ('REQUESTED', 'ACCEPTED', 'ARRIVED', 'IN_PROGRESS') ORDER BY r.requestedAt DESC")
    Optional<Ride> findActiveRideByUserId(@Param("userId") Long userId);
    
    /**
     * Find active ride for driver
     * @param driverId Driver ID
     * @return Optional containing active ride if found
     */
    @Query("SELECT r FROM Ride r WHERE r.driverId = :driverId AND r.status IN ('ACCEPTED', 'ARRIVED', 'IN_PROGRESS') ORDER BY r.acceptedAt DESC")
    Optional<Ride> findActiveRideByDriverId(@Param("driverId") Long driverId);
    
    /**
     * Find completed rides by user
     * @param userId User ID
     * @param pageable Pagination parameters
     * @return Page of completed rides
     */
    Page<Ride> findByUserIdAndStatus(Long userId, RideStatus status, Pageable pageable);
    
    /**
     * Find rides by vehicle type
     * @param vehicleType Vehicle type
     * @param pageable Pagination parameters
     * @return Page of rides
     */
    Page<Ride> findByVehicleType(VehicleType vehicleType, Pageable pageable);
    
    /**
     * Find rides requested within time range
     * @param startTime Start time
     * @param endTime End time
     * @return List of rides
     */
    List<Ride> findByRequestedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Count rides by user
     * @param userId User ID
     * @return Count of rides
     */
    long countByUserId(Long userId);
    
    /**
     * Count completed rides by driver
     * @param driverId Driver ID
     * @return Count of completed rides
     */
    long countByDriverIdAndStatus(Long driverId, RideStatus status);
    
    /**
     * Find rides that need auto-cancellation (REQUESTED for more than N minutes)
     * @param cutoffTime Cutoff time
     * @return List of rides to auto-cancel
     */
    @Query("SELECT r FROM Ride r WHERE r.status = 'REQUESTED' AND r.requestedAt < :cutoffTime")
    List<Ride> findRidesForAutoCancellation(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Check if user has any active rides
     * @param userId User ID
     * @param statuses List of ride statuses to check
     * @return true if user has active ride
     */
    boolean existsByUserIdAndStatusIn(Long userId, List<RideStatus> statuses);
    
    /**
     * Find rides by user ordered by requested time descending
     * @param userId User ID
     * @param pageable Pagination parameters
     * @return Page of rides
     */
    Page<Ride> findByUserIdOrderByRequestedAtDesc(Long userId, Pageable pageable);
}
