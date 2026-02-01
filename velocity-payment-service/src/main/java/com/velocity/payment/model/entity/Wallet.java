package com.velocity.payment.model.entity;

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
 * Wallet entity representing a user's wallet for payments.
 * Uses optimistic locking to prevent concurrent balance update issues.
 * 
 * Following rules.md:
 * - Uses @Data for getters/setters
 * - BigDecimal for monetary values
 * - @Version for optimistic locking
 * - Proper constraints
 */
@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Wallet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Version
    @Column(nullable = false)
    private Integer version = 0;
    
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
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        if (isActive == null) {
            isActive = true;
        }
        if (version == null) {
            version = 0;
        }
    }
    
    /**
     * Add money to wallet
     * @param amount Amount to add
     */
    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
    
    /**
     * Deduct money from wallet
     * @param amount Amount to deduct
     */
    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }
    
    /**
     * Check if wallet has sufficient balance
     * @param amount Amount to check
     * @return true if balance is sufficient
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }
}
