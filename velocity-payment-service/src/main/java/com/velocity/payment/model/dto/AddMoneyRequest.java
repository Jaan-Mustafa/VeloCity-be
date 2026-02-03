package com.velocity.payment.model.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding money to wallet.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMoneyRequest {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1.0")
    private BigDecimal amount;
    
    private String paymentMethod; // CARD, UPI, NET_BANKING (dummy for now)
    private String referenceId;
}
