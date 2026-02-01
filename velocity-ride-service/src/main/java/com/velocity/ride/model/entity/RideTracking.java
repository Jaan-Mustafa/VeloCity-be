package com.velocity.ride.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * RideTracking entity for storing location points during an active ride.
 * Used to track the route taken and for analytics.
 * 
 * Following rules.md:
 * - Uses @Data for getters/setters
 * - BigDecimal for precise coordinates
 * - ManyToOne relationship with Ride
 */
@Entity
@Table(name = "ride_tracking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideTracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ride_id", nullable = false)
    private Long rideId;
    
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal bearing;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal speed;
    
    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
    
    /**
     * Pre-persist callback to set timestamp
     */
    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }
}
