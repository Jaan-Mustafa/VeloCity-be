package com.velocity.core.exception;

import java.math.BigDecimal;

import com.velocity.core.constants.ErrorCodes;

import lombok.Getter;

/**
 * Exception thrown when a user has insufficient balance for a transaction.
 * Extends BusinessException with specific payment-related context.
 * 
 * @author VeloCity Team
 * @since 1.0.0
 */
@Getter
public class InsufficientBalanceException extends BusinessException {
    
    private final BigDecimal requiredAmount;
    private final BigDecimal availableBalance;
    private final BigDecimal shortfall;
    
    /**
     * Constructor with required and available amounts.
     *
     * @param requiredAmount the amount required for the transaction
     * @param availableBalance the current available balance
     */
    public InsufficientBalanceException(BigDecimal requiredAmount, BigDecimal availableBalance) {
        super(
            String.format(
                "Insufficient balance. Required: ₹%.2f, Available: ₹%.2f, Shortfall: ₹%.2f",
                requiredAmount,
                availableBalance,
                requiredAmount.subtract(availableBalance)
            ),
            ErrorCodes.INSUFFICIENT_BALANCE
        );
        this.requiredAmount = requiredAmount;
        this.availableBalance = availableBalance;
        this.shortfall = requiredAmount.subtract(availableBalance);
    }
    
    /**
     * Constructor with custom message and amounts.
     *
     * @param message custom error message
     * @param requiredAmount the amount required for the transaction
     * @param availableBalance the current available balance
     */
    public InsufficientBalanceException(String message, BigDecimal requiredAmount, BigDecimal availableBalance) {
        super(message, ErrorCodes.INSUFFICIENT_BALANCE);
        this.requiredAmount = requiredAmount;
        this.availableBalance = availableBalance;
        this.shortfall = requiredAmount.subtract(availableBalance);
    }
}
