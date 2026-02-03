package com.velocity.payment.model.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for deducting money from wallet.
 * Used internally by services (e.g., Ride Service).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductMoneyRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    private Long rideId;
    private String description;
}
