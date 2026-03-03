package com.velocity.core.enums;

import lombok.Getter;

/**
 * Payment status for ride payments.
 * Tracks the payment lifecycle separately from ride status.
 */
@Getter
public enum PaymentStatus {

    PENDING("Payment Pending", "Awaiting payment from rider"),
    PAID("Paid", "Payment completed successfully"),
    FAILED("Payment Failed", "Payment processing failed");

    private final String displayName;
    private final String description;

    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Check if payment is pending.
     * @return true if payment has not been completed
     */
    public boolean isPending() {
        return this == PENDING;
    }

    /**
     * Check if payment is complete.
     * @return true if payment was successful
     */
    public boolean isComplete() {
        return this == PAID;
    }

    /**
     * Check if payment failed.
     * @return true if payment processing failed
     */
    public boolean isFailed() {
        return this == FAILED;
    }
}
