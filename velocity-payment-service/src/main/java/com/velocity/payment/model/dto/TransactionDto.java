package com.velocity.payment.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.velocity.core.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Transaction information.
 * Used for API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    
    private Long id;
    private Long walletId;
    private Long rideId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private String referenceId;
    private String status;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}
