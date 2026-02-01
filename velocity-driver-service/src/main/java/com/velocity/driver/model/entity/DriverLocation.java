package com.velocity.driver.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DriverLocation entity for tracking real-time driver locations.
 * Used for finding nearby drivers and tracking during rides.
 * 
 * Following rules.md:
 * - Uses @Data for getters/setters
 * - BigDecimal for precise coordinates
 * - OneToOne relationship with Driver
 */
@Entity
@Table(name = "driver_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocation {
    
    @Id
    @Column(name = "driver_id")
    private Long driverId;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "driver_id")
    private Driver driver;
    
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal bearing;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal speed;
    
    @Column(precision = 6, scale = 2)
    private BigDecimal accuracy;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Pre-persist and pre-update callback to set timestamp
     */
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
