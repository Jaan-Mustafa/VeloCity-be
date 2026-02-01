package com.velocity.core.enums;

import lombok.Getter;

/**
 * Transaction types for wallet operations in the VeloCity system.
 * Each type has a multiplier to determine if it adds or subtracts from balance.
 */
@Getter
public enum TransactionType {
    
    CREDIT("Credit", 1, "Money added to wallet"),
    DEBIT("Debit", -1, "Money deducted from wallet"),
    REFUND("Refund", 1, "Money refunded to wallet");
    
    private final String displayName;
    private final int multiplier;
    private final String description;
    
    TransactionType(String displayName, int multiplier, String description) {
        this.displayName = displayName;
        this.multiplier = multiplier;
        this.description = description;
    }
    
    /**
     * Check if this is a credit transaction (adds money).
     * @return true if transaction type is CREDIT or REFUND
     */
    public boolean isCredit() {
        return multiplier > 0;
    }
    
    /**
     * Check if this is a debit transaction (removes money).
     * @return true if transaction type is DEBIT
     */
    public boolean isDebit() {
        return multiplier < 0;
    }
    
    /**
     * Get the multiplier for balance calculation.
     * @return 1 for credit/refund, -1 for debit
     */
    public int getMultiplier() {
        return multiplier;
    }
}
