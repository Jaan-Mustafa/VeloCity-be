package com.velocity.driver.model.entity;

import com.velocity.core.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Driver entity representing a driver profile with vehicle information.
 * 
 * Following rules.md:
 * - Uses @Data for getters/setters
 * - Uses BigDecimal for monetary values
 * - Enum stored as STRING
 * - Proper validation constraints
 */
@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 20)
    private VehicleType vehicleType;
    
    @Column(name = "vehicle_model", nullable = false, length = 100)
    private String vehicleModel;
    
    @Column(name = "vehicle_number", nullable = false, unique = true, length = 20)
    private String vehicleNumber;
    
    @Column(name = "vehicle_color", length = 50)
    private String vehicleColor;
    
    @Column(name = "vehicle_year")
    private Integer vehicleYear;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = false;
    
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
    
    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = new BigDecimal("5.00");
    
    @Column(name = "total_rides", nullable = false)
    private Integer totalRides = 0;
    
    @Column(name = "total_earnings", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalEarnings = BigDecimal.ZERO;
    
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
        if (isAvailable == null) {
            isAvailable = false;
        }
        if (isVerified == null) {
            isVerified = false;
        }
        if (rating == null) {
            rating = new BigDecimal("5.00");
        }
        if (totalRides == null) {
            totalRides = 0;
        }
        if (totalEarnings == null) {
            totalEarnings = BigDecimal.ZERO;
        }
    }
}
