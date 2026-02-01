package com.velocity.ride.model.entity;

import com.velocity.core.enums.RideStatus;
import com.velocity.core.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ride entity representing a ride request and its complete lifecycle.
 * Tracks all stages from REQUESTED to COMPLETED/CANCELLED.
 * 
 * Following rules.md:
 * - Uses @Data for getters/setters
 * - BigDecimal for monetary values and coordinates
 * - Enums stored as STRING
 * - Comprehensive lifecycle tracking
 */
@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Ride {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "driver_id")
    private Long driverId;
    
    // Pickup location
    @Column(name = "pickup_latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal pickupLatitude;
    
    @Column(name = "pickup_longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal pickupLongitude;
    
    @Column(name = "pickup_address", length = 255)
    private String pickupAddress;
    
    // Dropoff location
    @Column(name = "dropoff_latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal dropoffLatitude;
    
    @Column(name = "dropoff_longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal dropoffLongitude;
    
    @Column(name = "dropoff_address", length = 255)
    private String dropoffAddress;
    
    // Ride details
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RideStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 20)
    private VehicleType vehicleType;
    
    // Pricing and metrics
    @Column(precision = 10, scale = 2)
    private BigDecimal fare;
    
    @Column(name = "distance_km", precision = 10, scale = 2)
    private BigDecimal distanceKm;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    // Cancellation
    @Column(name = "cancelled_by", length = 20)
    private String cancelledBy;
    
    @Column(name = "cancellation_reason", length = 255)
    private String cancellationReason;
    
    @Column(name = "cancellation_fee", precision = 10, scale = 2)
    private BigDecimal cancellationFee = BigDecimal.ZERO;
    
    // Ratings
    @Column(name = "rider_rating", precision = 3, scale = 2)
    private BigDecimal riderRating;
    
    @Column(name = "driver_rating", precision = 3, scale = 2)
    private BigDecimal driverRating;
    
    @Column(name = "rider_feedback", length = 500)
    private String riderFeedback;
    
    @Column(name = "driver_feedback", length = 500)
    private String driverFeedback;
    
    // Timestamps
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;
    
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    
    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Pre-persist callback to set default values
     */
    @PrePersist
    protected void onCreate() {
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
        if (cancellationFee == null) {
            cancellationFee = BigDecimal.ZERO;
        }
    }
}
