package com.velocity.payment.model.entity;

import com.velocity.core.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Transaction entity representing a wallet transaction.
 * Stores complete audit trail with balance before/after.
 *
 * Following rules.md:
 * - Uses @Data for getters/setters
 * - BigDecimal for monetary values
 * - JSONB for flexible metadata (using Hibernate 6 native support)
 * - Immutable after creation (no updates)
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "wallet_id", nullable = false)
    private Long walletId;
    
    @Column(name = "ride_id")
    private Long rideId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "balance_before", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceBefore;
    
    @Column(name = "balance_after", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAfter;
    
    @Column(length = 255)
    private String description;
    
    @Column(name = "reference_id", length = 100)
    private String referenceId;
    
    @Column(nullable = false, length = 20)
    private String status = "COMPLETED";
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Pre-persist callback to set timestamp
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "COMPLETED";
        }
    }
}
